package type.filetype;

import type.DataType;

import java.io.File;

public class MetisFile implements DataType {
    private File file;
    private int indexOffset;

    public MetisFile(String path) {
        this(path, 0);
    }

    public MetisFile(String path, int indexOffset) {
        this.file = new File(path);
        this.indexOffset = indexOffset;

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

    public int getIndexOffset() {
        return indexOffset;
    }
}
