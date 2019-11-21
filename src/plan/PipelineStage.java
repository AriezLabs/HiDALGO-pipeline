package plan;

import excption.BadConfigException;
import log.Log;
import process.Stage;
import type.DataType;

import java.util.Map;

/**
 * Wrapper for an individual algorithm or stage representing a stage in the pipeline. Instantiates the stage via
 * reflection and configures it according to parameters. Times how long the stage took.
 */
class PipelineStage {
    private Stage stage;
    private String name;

    PipelineStage(String processName, Map<String, String> params) throws Exception {
        name = processName;

        stage = (Stage) Class.forName("process." + processName).getConstructor().newInstance();
        try {
            stage.configure(params);
        } catch (BadConfigException e) {
            throw new BadConfigException(String.format("at stage %s: %s", name, e.getMessage()));
        }
    }

    DataType execute(DataType input) throws Exception {
        long startTime = System.currentTimeMillis();
        Log.info(String.format("starting stage %s", name));

        DataType result;
        try {
            result = stage.execute(input);
        } catch (ClassCastException e) {
            System.err.println("Class cast exception: likely mismatching input types for validation and execute function!");
            throw e;
        }

        long execTime = System.currentTimeMillis() - startTime;
        Log.info(String.format("stage %s completed in %fs", name, (0.001d * execTime)));
        return result;
    }

    Class getReturnType(){
        return stage.getReturnType();
    }

    Class getInputType(){
        return stage.getInputType();
    }

    String getName() {
        return name;
    }
}
