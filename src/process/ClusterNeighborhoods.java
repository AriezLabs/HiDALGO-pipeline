package process;

import excption.BadConfigException;
import log.Log;
import type.DataType;
import type.Neighborhoods;
import type.filetype.NodelistFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Take Neighborhoods object,
 * cluster each neighborhood using NetworKit
 * Params:
 *     nthreads (default 4)
 *     communityMinSize (default 1)
 *     outfile (default communities.nl)
 *     pythonBinary (default /usr/bin/python)
 */

public class ClusterNeighborhoods extends PythonScript implements Stage {
    public static final String scriptPath = "python/ClusterNeighborhoods.py";

    private static final String nthreadsParamName = "threads";
    private static final String communityMinSizeParamName = "communityminsize";
    private static final String outfileParamName = "outfile";
    private static final String pythonBinaryParamName = "pythonbinary";

    private String nthreads;
    private String communityMinSize;
    private String outfile;

    @Override
    public Class getInputType() {
        return Neighborhoods.class;
    }

    @Override
    public Class getReturnType() {
        return NodelistFile.class;
    }

    @Override
    public void configure(Map<String, String> params) {
        if (params.get(pythonBinaryParamName) != null)
            PythonScript.pythonBinary = params.get(pythonBinaryParamName);
        if ((outfile = params.get(outfileParamName)) == null)
            outfile = "communities.nl";
        if ((communityMinSize = params.get(communityMinSizeParamName)) == null)
            communityMinSize = "1";
        if ((nthreads = params.get(nthreadsParamName)) == null)
            nthreads = "2";
    }

    // possible improvement: spawn multiple Python processes
    @Override
    public DataType execute(DataType uncastedInput) throws Exception {
        Neighborhoods in = (Neighborhoods) uncastedInput;

        String outFifoPath = mkfifo("JPyPipe");
        String inFifoPath = mkfifo("PyJPipe");

        AtomicInteger exitcode = new AtomicInteger();

        Thread t = new Thread(() -> {
            try {
                exitcode.set(run(scriptPath, outFifoPath, inFifoPath, nthreads, communityMinSize, outfile));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        t.start();

        for (int i = 0; i < in.getNumNeighborhoods(); i++) {
            in.toPipe(outFifoPath, i);
            Log.log("item: " + i + "\r", Log.DEBUG, 2, false);
            waitForOK(inFifoPath);
        }

        writeEndSignal(outFifoPath);
        t.join();

        new File(outFifoPath).delete();
        new File(inFifoPath).delete();

        if(exitcode.get() == 0)
            return new NodelistFile(outfile, in.getParentGraph());
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
        raf.readLine();
        raf.close();
    }

    private String mkfifo(String name) throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
        String dir = System.getProperty("user.dir") + "/";
        pb.command("mkfifo", dir + name);
        pb.inheritIO().start();
        return dir + name;
    }
}
