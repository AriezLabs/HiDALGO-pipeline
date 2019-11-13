package process;

import excption.BadConfigException;
import type.DataType;
import type.filetype.MetisFile;

import java.util.Map;

public class AddZeroNode extends PythonScript implements Stage {
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

        String path = in.getFile().getAbsolutePath();
        String outPath = path + ".WithCentralNode.metis";
        int exitcode = run("python/AddZeroNode.py", path, outPath);

        if(exitcode == 0)
            return new MetisFile(outPath, 0);
        else
            throw new RuntimeException("AddZeroNode python script exited with bad code " + exitcode);
    }
}
