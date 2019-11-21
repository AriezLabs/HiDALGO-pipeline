package type;

public interface DataType {
    default void save(String path) {
        throw new UnsupportedOperationException();
    }
}
