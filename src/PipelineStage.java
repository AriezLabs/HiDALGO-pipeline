import process.BadConfigException;
import type.DataType;
import process.Process;

import java.util.HashMap;

public class PipelineStage {
    private Process process;
    private String name;

    public PipelineStage(String configString) throws BadConfigException {
        HashMap<String, String> params = new HashMap<>();

        try {
            process = (Process) Class.forName("Process").newInstance();
        } catch (ClassNotFoundException e) {
            throw new BadConfigException("unknown pipeline stage");
        } catch (Exception e) {

        }

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
