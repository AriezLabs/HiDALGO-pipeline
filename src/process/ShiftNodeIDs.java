package process;

import excption.BadConfigException;
import type.DataType;
import type.filetype.MetisFile;

import java.util.Map;

public class ShiftNodeIDs extends PythonScript implements Stage {
    public static final String scriptPath = "python/ShiftNodeIDs.py";
    private String delta;

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
        if ((delta = params.get("delta")) == null)
            delta = "0";
    }

    @Override
    public DataType execute(DataType uncastedInput) throws Exception {
        MetisFile in = (MetisFile) uncastedInput;

        String path = in.getFile().getAbsolutePath();
        String outPath = String.format("%sshiftedBy%s.metis", path, delta);
        int exitcode = run(scriptPath, path, outPath, delta);

        if(exitcode == 0)
            return new MetisFile(outPath, 0);
        else
            throw new RuntimeException("ShiftNodeIDs python script exited with bad code " + exitcode);
    }
}
