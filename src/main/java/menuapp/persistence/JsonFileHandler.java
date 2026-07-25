package menuapp.persistence;

/**
 * JSON implementation of {@link FileHandler} using Jackson. Catches
 * {@code IOException} and rethrows it as an unchecked {@link RuntimeException}
 * with the failure as the cause, so {@code IOException} never leaks above
 * persistence.
 */
public class JsonFileHandler implements FileHandler {

  @Override
  public <T> void save(T data, String filePath) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public <T> T load(String filePath, Class<T> type) {
    throw new UnsupportedOperationException("TODO");
  }
}
