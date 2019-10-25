package process;

import process.exception.BadConfigException;
import type.DataType;

import java.util.Map;

public class ReadInput implements Process{
    @Override
    public void configure(Map<String, String> params) throws BadConfigException {
    }

    @Override
    public DataType execute(DataType input) throws Exception {
        return null;
    }
}
