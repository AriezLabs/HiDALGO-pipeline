package process;

import excption.BadConfigException;
import type.DataType;

import java.util.Map;

public interface Stage {
    // for validation
    Class getInputType();
    Class getReturnType();

    /**
     * Configure process to use parameters given in a dictionary
     * @param params the params
     * @throws BadConfigException if something does not make sense
     */
    void configure(Map<String, String> params) throws BadConfigException;

    /**
     * Execute process on the input. Must be casted to InputType first
     */
    DataType execute(DataType uncastedInput) throws Exception;
}
