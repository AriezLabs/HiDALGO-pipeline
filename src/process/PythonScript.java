package process;

import log.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

abstract class PythonScript {

    /**
     * run a python script
     * @param script Path to python script
     * @param params params for script
     * @return exit code of script
     * @throws Exception if script does not exist
     */
    int run(String script, String... params) throws Exception {
        String[] command = new String[params.length + 2];
        command[0] = "/usr/bin/python";
        command[1] = script;
        System.arraycopy(params, 0, command, 2, params.length);

        ProcessBuilder pb = new ProcessBuilder(command);

        Log.info(String.format("executing: %s", pb.command().stream().reduce("", (a, b) -> a + " " + b)), 1);

        Process p = pb.start();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null)
                Log.debug(line, 2);
        }

        return p.waitFor();
    }
}
