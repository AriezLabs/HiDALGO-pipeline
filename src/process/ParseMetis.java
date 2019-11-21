package process;

import excption.BadConfigException;
import type.DataType;
import type.Graph;
import type.filetype.MetisFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
        File in = ((MetisFile) uncastedInput).getFile();

        try (BufferedReader br = new BufferedReader(new FileReader(in))) {

        }

        return null;
    }
}
