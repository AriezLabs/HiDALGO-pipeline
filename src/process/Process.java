package process;

import type.DataType;

import java.util.Map;

public interface Process {
    void configure(Map<String, String> params) throws BadConfigException;

    DataType execute(DataType input) throws Exception;
}
