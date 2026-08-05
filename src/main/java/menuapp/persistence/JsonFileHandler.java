package menuapp.persistence;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;

/**
 * JSON implementation of {@link FileHandler} backed by a Jackson
 * {@link ObjectMapper}. One instance serves every save and load in the app:
 * the customer {@code FavoritesList} and the staff low stock sub-list both
 * round trip through the same generic pair.
 *
 * <p>The mapper is configured once in the constructor and reused for every
 * call, which is safe because a configured {@code ObjectMapper} is thread
 * safe for reading and writing. It is set up to
 * <ul>
 *   <li>write indented, human readable JSON, so saved files are easy to
 *       inspect and diff, and</li>
 *   <li>ignore unknown properties on load, so a file written by a slightly
 *       older or newer version of a model class still loads instead of
 *       failing the whole read.</li>
 * </ul>
 *
 * <p><b>Exception policy.</b> Jackson signals every read and write failure as
 * an {@link IOException} (its own {@code JsonProcessingException} is a
 * subclass). This class catches that checked exception and rethrows it as an
 * unchecked {@link RuntimeException} that carries the original failure as its
 * cause. As a result {@code IOException} never leaks above the persistence
 * package and no method needs a {@code throws} clause, while a caller that
 * wants to react, such as a panel showing a {@code JOptionPane}, can still
 * catch the {@code RuntimeException} and read its cause.
 */
public class JsonFileHandler implements FileHandler {

  /** Configured, reused mapper for every read and write. */
  private final ObjectMapper mapper;

  /**
   * Creates a handler whose mapper writes indented JSON and tolerates unknown
   * properties when reading.
   */
  public JsonFileHandler() {
    this.mapper = new ObjectMapper();
    this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }

  /**
   * Writes any model object to a file as JSON. An existing file at
   * {@code filePath} is overwritten.
   *
   * @param data the object to save, for example a {@code FavoritesList} or the
   *     low stock list
   * @param filePath where to write it
   * @param <T> the type of the object
   * @throws RuntimeException if the write fails, with the underlying
   *     {@link IOException} as its cause
   */
  @Override
  public <T> void save(T data, String filePath) {
    try {
      mapper.writeValue(new File(filePath), data);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save data to " + filePath, e);
    }
  }

  /**
   * Reads a model object back from a JSON file.
   *
   * @param filePath the file to read
   * @param type the class to load the JSON into
   * @param <T> the type of the object
   * @return the loaded object
   * @throws RuntimeException if the file is missing, unreadable, or does not
   *     hold JSON that maps to {@code type}, with the underlying
   *     {@link IOException} as its cause
   */
  @Override
  public <T> T load(String filePath, Class<T> type) {
    try {
      return mapper.readValue(new File(filePath), type);
    } catch (IOException e) {
      throw new RuntimeException(
              "Failed to load " + type.getSimpleName() + " from " + filePath, e);
    }
  }
}