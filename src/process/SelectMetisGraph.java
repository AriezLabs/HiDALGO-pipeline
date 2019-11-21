package process;

import excption.BadConfigException;
import type.DataType;
import type.filetype.MetisFile;

import java.util.Map;

/**
 * Takes no input. Creates a new MetisFile and passes it on
 */
public class SelectMetisGraph implements Stage {
    private final static String pathParam = "path";
    private final static String indexOffsetParam = "offset";

    private String path;
    private int indexOffset;

    @Override
    public Class getInputType() {
        return null;
    }

    @Override
    public Class getReturnType() {
        return MetisFile.class;
    }

    @Override
    public void configure(Map<String, String> params) throws BadConfigException {
        if ((path = params.get(pathParam)) == null)
            throw new BadConfigException(String.format("required param %s not set", pathParam));

        if (params.get(indexOffsetParam) == null) {
            indexOffset = 1;
        } else {
            indexOffset = Integer.parseInt(params.get(indexOffsetParam));
        }
    }

    @Override
    public DataType execute(DataType uncastedInput) throws Exception {
        return new MetisFile(path, indexOffset);
    }
}
