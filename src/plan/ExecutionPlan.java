package plan;

import type.DataType;

import java.io.*;

/**
 * A list of PipelineStages. This class ties them together and offers a unified interface for creating a pipeline,
 * sending data through it and retrieving the result.
 */
public class ExecutionPlan {
    private DataType currentData;
    private PipelineStage[] stages;

    public ExecutionPlan(String configPath) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(configPath)))) {
            stages = Parser.parse(br);
        }
    }

    public void execute() throws Exception {
        for(PipelineStage stage : stages)
            currentData = stage.execute(currentData);
    }

    public DataType getCurrentData() {
        return currentData;
    }

    public int numStages() {
        return stages.length;
    }
}
