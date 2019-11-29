package process;

import excption.BadConfigException;
import log.Log;
import type.DataType;
import type.Neighborhoods;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ClusterNeighborhoods extends PythonScript implements Stage {
    public static final String scriptPath = "test/testPyFile.py";

    private static final  String nthreadsParamName = "threads";

    private String nthreads;

    @Override
    public Class getInputType() {
        return Neighborhoods.class;
    }

    @Override
    public Class getReturnType() {
        return null;
    }

    @Override
    public void configure(Map<String, String> params) throws BadConfigException {
        if ((nthreads = params.get(nthreadsParamName)) == null)
            nthreads = "1";
    }

    @Override
    public DataType execute(DataType uncastedInput) throws Exception {
        Neighborhoods in = (Neighborhoods) uncastedInput;

        String outFifoPath = "/tmp/test";
        String inFifoPath = "/tmp/test2";

        AtomicInteger exitcode = new AtomicInteger();

        Thread t = new Thread(() -> {
            try {
                exitcode.set(run(scriptPath, outFifoPath, inFifoPath, nthreads));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        t.start();

        for (int i = 0; i < in.getNumNeighborhoods(); i++) {
            in.toPipe(outFifoPath, i);
            Log.log("item: " + i + "\r", Log.INFO, 2, false);
            waitForOK(inFifoPath);
        }

        writeEndSignal(outFifoPath);
        t.join();

        if(exitcode.get() == 0)
            return null;//new MetisFile(outPath, 0);
        else
            throw new RuntimeException("ClusterNeighborhoods python script exited with bad code " + exitcode);
    }

    private void writeEndSignal(String path) throws IOException, InterruptedException {
        FileWriter fw = new FileWriter(new File(path));
        fw.write("END\n");
        fw.close();
    }

    private void waitForOK(String path) throws IOException, InterruptedException {
        File  f_pipe = new File (path);
        RandomAccessFile raf = new RandomAccessFile(f_pipe, "r");  // point 1
        String line = raf.readLine();
        raf.close();
    }
}
