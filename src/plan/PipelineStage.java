package plan;

import log.Log;
import process.Process;
import type.DataType;

import java.util.Map;

public class PipelineStage {
    private Process process;
    private String name;

    public PipelineStage(String processName, Map<String, String> params) throws Exception {
        name = processName;

        process = (Process) Class.forName("process." + processName).getConstructor().newInstance();
        process.configure(params);
    }

    public DataType execute(DataType input) throws Exception {
        long startTime = System.currentTimeMillis();
        Log.info(String.format("starting stage %s", name));

        DataType result = process.execute(input);

        Log.info(String.format("stage %s completed in %ds", name, (int) (0.001d*(System.currentTimeMillis() - startTime))));
        return result;
    }
}
