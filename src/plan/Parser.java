package plan;

import process.exception.BadConfigException;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.LinkedList;

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

        return stages.toArray(new PipelineStage[0]);
    }

    private static PipelineStage parseProcess(StreamTokenizer st) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        String processName = next(st);

        if (processName != null) {
            String currentSymbol = next(st);

            if (currentSymbol.equals("{")) {

                while (!(currentSymbol = next(st)).equals("}")) {
                    String value = next(st);

                    if (!value.equals("}"))
                        params.put(currentSymbol, value);

                    // Error: param name not followed by value
                    else
                        throw new BadConfigException(String.format("expected value for param %s, got %s", currentSymbol, value));
                }

                return new PipelineStage(processName, params);

            // Error: process name not followed by { symbol
            } else {
                throw new BadConfigException("expected {, got " + currentSymbol);
            }
        }

        return null;
    }

    /**
     * @return Null if EOF or exception.
     */
    private static String next(StreamTokenizer st) throws IOException {
        st.nextToken();

        switch (st.ttype) {
            case StreamTokenizer.TT_NUMBER:
                return st.nval + "";
            case StreamTokenizer.TT_WORD:
                return st.sval;
            default:
                return null;
        }
    }
}
