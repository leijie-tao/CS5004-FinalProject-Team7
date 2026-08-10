package menuapp.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link JsonFileHandler}.
 *
 * <p>These tests exercise the persistence seam on its own, using a small local
 * fixture ({@link Widget}) and a plain {@code List<String>}. The list case
 * mirrors the real low stock export, which is a list of item names. Testing
 * against fixtures rather than the model classes keeps these tests independent
 * of when the model package is finished, which is exactly why the persistence
 * layer is behind an interface.
 */
class JsonFileHandlerTest {

  private FileHandler handler;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    handler = new JsonFileHandler();
  }

  // ---------- happy path ----------

  @Test
  void saveThenLoadRoundTripsAnObject() {
    Widget original = new Widget("latte", 3);
    String path = tempDir.resolve("widget.json").toString();

    handler.save(original, path);
    Widget loaded = handler.load(path, Widget.class);

    assertEquals(original, loaded);
  }

  @Test
  void saveThenLoadRoundTripsAListOfStrings() {
    // Same shape as the exported low stock sub-list: a list of item names.
    List<String> lowStock = List.of("Fries", "Cola", "Cheesecake");
    String path = tempDir.resolve("lowstock.json").toString();

    handler.save(lowStock, path);
    List<?> loaded = handler.load(path, List.class);

    assertEquals(lowStock, loaded);
  }

  @Test
  void saveWritesANonEmptyIndentedFile() throws IOException {
    String path = tempDir.resolve("widget.json").toString();

    handler.save(new Widget("latte", 3), path);
    String written = Files.readString(Path.of(path));

    assertTrue(written.contains("latte"), "file should contain the saved data");
    // INDENT_OUTPUT is enabled, so a multi field object spans several lines.
    assertTrue(written.contains("\n"), "output should be pretty printed");
  }

  // ---------- failure path: every failure is a RuntimeException whose
  //            cause is the original IOException ----------

  @Test
  void loadMissingFileThrowsRuntimeExceptionCausedByIoException() {
    String path = tempDir.resolve("does-not-exist.json").toString();

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> handler.load(path, Widget.class));

    assertNotNull(ex.getCause());
    assertTrue(ex.getCause() instanceof IOException,
        "cause should be the underlying IOException, was " + ex.getCause());
  }

  @Test
  void loadMalformedJsonThrowsRuntimeExceptionCausedByIoException() throws IOException {
    Path path = tempDir.resolve("broken.json");
    Files.writeString(path, "{ this is not valid json ");

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> handler.load(path.toString(), Widget.class));

    assertTrue(ex.getCause() instanceof IOException);
  }

  @Test
  void saveToUnwritablePathThrowsRuntimeExceptionCausedByIoException() {
    // Parent directory does not exist, so the write cannot open the file.
    String path = tempDir.resolve("no-such-dir").resolve("widget.json").toString();

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> handler.save(new Widget("latte", 3), path));

    assertTrue(ex.getCause() instanceof IOException);
  }

  /**
   * Minimal Jackson friendly fixture: a no argument constructor plus getters
   * and setters. It stands in for a real model object so these tests do not
   * depend on the model package being implemented.
   */
  static class Widget {
    private String name;
    private int quantity;

    /** No argument constructor required by Jackson for deserialization. */
    Widget() {
    }

    Widget(String name, int quantity) {
      this.name = name;
      this.quantity = quantity;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getQuantity() {
      return quantity;
    }

    public void setQuantity(int quantity) {
      this.quantity = quantity;
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!(other instanceof Widget)) {
        return false;
      }
      Widget w = (Widget) other;
      return quantity == w.quantity && Objects.equals(name, w.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, quantity);
    }
  }
}
