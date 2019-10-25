package process;

import process.exception.BadConfigException;
import type.DataType;

import java.util.Map;

public interface Process {
    // Parse parameters
    void configure(Map<String, String> params) throws BadConfigException;

    // Execute process on the input. Cast to whatever is needed.
    DataType execute(DataType input) throws Exception;
}
