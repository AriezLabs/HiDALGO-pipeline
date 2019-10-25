package plan;

import log.Log;
import process.Process;
import type.DataType;

import java.util.Map;

/**
 * Wrapper for an individual algorithm or process representing a stage in the pipeline. Instantiates the stage via
 * reflection and configures it according to parameters. Times how long the stage took.
 */
public class PipelineStage {
    private Process process;
    private String name;
    private long execTime;

    public PipelineStage(String processName, Map<String, String> params) throws Exception {
        name = processName;

        process = (Process) Class.forName("process." + processName).getConstructor().newInstance();
        process.configure(params);
    }

    public DataType execute(DataType input) throws Exception {
        long startTime = System.currentTimeMillis();
        Log.info(String.format("starting stage %s", name));

        DataType result = process.execute(input);

        execTime = System.currentTimeMillis() - startTime;
        Log.info(String.format("stage %s completed in %ds", name, (int) (0.001d * execTime)));
        return result;
    }
}
