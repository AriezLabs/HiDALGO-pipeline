package type.filetype;

import type.DataType;
import type.Graph;

import java.io.File;

public class NodelistFile implements DataType {
    private final String parent;
    private File file;
    private int indexOffset;

    public NodelistFile(String path, String parentGraph) {
        this.parent = parentGraph;
        this.file = new File(path);

        if(!file.exists())
            throw new RuntimeException(String.format("cannot instantiate MetisFile from %s: File does not exist", path));
    }

    /**
     * Does nothing, file should be saved already.
     */
    @Override
    public void save(String path) { }

    public File getFile() {
        return file;
    }

    public String getParentGraph() {
        return parent;
    }
}
