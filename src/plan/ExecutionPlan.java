package plan;

import type.DataType;

import java.io.*;

/**
 * A list of Process classes representing stages in the pipeline.
 * Offers methods tying them together.
 * Parsing is delegated to parser.PipelineStage
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
}
