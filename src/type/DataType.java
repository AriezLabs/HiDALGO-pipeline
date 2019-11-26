package type;

import java.io.IOException;

public interface DataType {
    default void save(String path) throws IOException {
        throw new UnsupportedOperationException();
    }
}
