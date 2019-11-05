package process;

import excption.BadConfigException;
import log.Log;
import type.DataType;
import type.filetype.MetisFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

public class AddZeroNode implements Stage {
    public static final String scriptPath = "python/AddZeroNode.py";

    @Override
    public Class getInputType() {
        return MetisFile.class;
    }

    @Override
    public Class getReturnType() {
        return MetisFile.class;
    }

    @Override
    public void configure(Map<String, String> params) throws BadConfigException {

    }

    @Override
    public DataType execute(DataType uncastedInput) throws Exception {
        MetisFile in = (MetisFile) uncastedInput;

        if (in.getIndexOffset() != 1)
            throw new RuntimeException("AddZeroNode requires index offset of exactly one for Metis graphs");

        ProcessBuilder pb = new ProcessBuilder("python", scriptPath, in.getFile().getAbsolutePath());

        Log.info(String.format("executing: %s", pb.command().stream().reduce("", (a, b) -> a + " " + b)), 1);

        Process p = pb.start();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null)
                Log.debug(line, 2);
        }

        int exitcode = p.waitFor();

        if(exitcode == 0)
            return new MetisFile(in.getFile().getPath(), 0);
        else
            throw new RuntimeException("AddZeroNode python script exited with bad code " + exitcode);
    }
}
