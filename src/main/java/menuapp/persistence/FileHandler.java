package menuapp.persistence;

/**
 * Reads and writes model objects to disk. This is the persistence seam,
 * so it can be mocked in tests. One generic pair serves the favorites list
 * and the low stock list.
 */
public interface FileHandler {

  /**
   * Writes any model object to a file.
   *
   * @param data the object to save, for example a FavoritesList or the low stock list
   * @param filePath where to write it
   * @param <T> the type of the object
   */
  <T> void save(T data, String filePath);

  /**
   * Reads a model object back from a file.
   *
   * @param filePath the file to read
   * @param type the class to load into
   * @param <T> the type of the object
   * @return the loaded object
   */
  <T> T load(String filePath, Class<T> type);
}
