package process;

import excption.BadConfigException;
import type.DataType;
import type.Graph;
import type.filetype.MetisFile;

import java.util.Map;

public class ParseMetis implements Stage {
    @Override
    public Class getInputType() {
        return MetisFile.class;
    }

    @Override
    public Class getReturnType() {
        return Graph.class;
    }

    @Override
    public void configure(Map<String, String> params) throws BadConfigException {

    }

    @Override
    public DataType execute(DataType uncastedInput) throws Exception {
        MetisFile in = (MetisFile) uncastedInput;
        return null;
    }
}
