import process.BadConfigException;
import type.DataType;

import java.util.LinkedList;

/**
 * A list of Process classes representing stages in the pipeline.
 * Offers methods tying them together.
 * Parsing is delegated to PipelineStage
 */
public class ExecutionPlan {
    private DataType input;
    private DataType output;
    private LinkedList<PipelineStage> stages;

    public ExecutionPlan(String configPath) throws BadConfigException {
        stages = new LinkedList<>();
    }

    public void execute() throws Exception {
        DataType current = input;

        for(PipelineStage stage : stages)
            current = stage.execute(current);

        output = current;
    }

    public DataType getOutput() {
        return output;
    }
}
