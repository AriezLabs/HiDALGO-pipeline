package process;

import excption.BadConfigException;
import type.DataType;
import type.filetype.MetisFile;

import java.util.Map;

public class ClusterNeighborhoods extends PythonScript implements Stage {
    public static final String scriptPath = "python/ClusterNeighborhoods.py";

    private static final  String nthreadsParamName = "threads";

    private String nthreads;

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
        if ((nthreads = params.get(nthreadsParamName)) == null)
            nthreads = "1";
    }

    @Override
    public DataType execute(DataType uncastedInput) throws Exception {
        MetisFile in = (MetisFile) uncastedInput;

        String path = in.getFile().getAbsolutePath();
        String outPath = path + "Communities.nls";
        int exitcode = run(scriptPath, path, outPath, nthreads);

        if(exitcode == 0)
            return null;//new MetisFile(outPath, 0);
        else
            throw new RuntimeException("AddZeroNode python script exited with bad code " + exitcode);
    }
}
