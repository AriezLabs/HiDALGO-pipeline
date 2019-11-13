package plan;

import log.Log;
import excption.BadConfigException;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Parser {
    public static PipelineStage[] parse(Reader plan) throws Exception {
        LinkedList<PipelineStage> stages = new LinkedList<>();

        StreamTokenizer st = new StreamTokenizer(plan);
        st.commentChar('#');
        st.wordChars(47,47);
        st.wordChars(123,123);
        st.wordChars(125,125);
        st.parseNumbers();

        PipelineStage s;
        while ((s = parseProcess(st)) != null)
            stages.add(s);

        validate(stages);

        return stages.toArray(new PipelineStage[0]);
    }

    private static void validate(List<PipelineStage> stages) throws BadConfigException {
        for (int i = 1; i < stages.size(); i++) {
            Class returnType = stages.get(i-1).getReturnType();
            Class inputType = stages.get(i).getInputType();

            if (returnType == null) {
                if (inputType != null)
                    throw new BadConfigException("stage " + stages.get(i-1).getName() + " returns nothing but next stage expects input");
                continue;
            }

            if (inputType == null){
                Log.warn(String.format("stage %d (%s) discards input from previous stage (%s)", i + 1, stages.get(i).getName(), stages.get(i-1).getName()));
                continue;
            }

            if (!returnType.getName().equals(inputType.getName()))
                throw new BadConfigException(String.format("incompatible return/input types at stage %d (%s), cannot pass %s to %s", i - 1, stages.get(i - 1).getName(), returnType.getName(), inputType.getName()));
        }

        if (stages.get(stages.size() - 1).getReturnType() == null)
            Log.warn("Last stage of pipeline returns null. No data will be saved for at least the last stage");
    }

    private static PipelineStage parseProcess(StreamTokenizer st) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        String processName = next(st);

        if (processName != null) {
            String currentSymbol = next(st);

            if (currentSymbol.equals("{")) {

                // currentSymbol is param name/key
                while (!(currentSymbol = next(st)).equals("}")) {
                    String value = next(st);

                    if (!value.equals("}"))
                        params.put(currentSymbol.toLowerCase(), value.toLowerCase());

                    // Error: param name not followed by value
                    else
                        throw new BadConfigException(String.format("expected value for param %s, got %s", currentSymbol, value));
                }

                try {
                    return new PipelineStage(processName, params);
                } catch (ClassNotFoundException e) {
                    throw new BadConfigException(processName + ": unknown/unimplemented process");
                }

            // Error: process name not followed by { symbol
            } else {
                throw new BadConfigException("expected {, got " + currentSymbol);
            }
        }
        else
            return null;
    }

    /**
     * @return Null if EOF or exception.
     */
    private static String next(StreamTokenizer st) throws IOException {
        st.nextToken();

        switch (st.ttype) {
            case StreamTokenizer.TT_NUMBER:
                if (st.nval % 1 == 0)
                    return (int) st.nval + "";
                else
                    return st.nval + "";
            case StreamTokenizer.TT_WORD:
                return st.sval;
            default:
                return null;
        }
    }
}
