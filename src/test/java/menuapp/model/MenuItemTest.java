package menuapp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for MenuItem.
 *
 * <p>Restored from the original {@code TestMenuItem.java}, which was
 * accidentally removed when PR #6 ("Situ model customers") was reverted
 * (commit 7a1a0fa). Recovered via {@code git show 7a1a0fa^:...} and moved
 * into the current {@code menuapp.model} test package to match the project's
 * updated test layout.
 */
public class MenuItemTest {

  /** Verifies every getter returns the value passed to the constructor. */
  @Test
  public void gettersReturnValue() {
    MenuItem item = new MenuItem("Salad", 14.49, Category.MAIN, "images/salad.png");
    assertEquals("Salad", item.getName());
    assertEquals(14.49, item.getPrice());
    assertEquals(Category.MAIN, item.getCategory());
    assertEquals("images/salad.png", item.getImagePath());
  }

  /**
   * Verifies two items with the same name are equal even when price,
   * category, and image differ. Identity is name-only by design.
   */
  @Test
  void itemsWithSameName() {
    MenuItem item1 = new MenuItem("Burger", 8.99, Category.MAIN, "images/burger.png");
    MenuItem item2 = new MenuItem("Burger", 12.50, Category.DESSERT, null);
    assertEquals(item1, item2);
  }

  /** Verifies items with different names are never equal. */
  @Test
  void itemsWithDifferentNames() {
    MenuItem item1 = new MenuItem("Burger", 8.99, Category.MAIN, null);
    MenuItem item2 = new MenuItem("Fries", 8.99, Category.MAIN, null);
    assertNotEquals(item1, item2);
  }

  /** Verifies equal items (same name) also produce equal hash codes. */
  @Test
  void equalItemsHaveSameHashCode() {
    MenuItem item1 = new MenuItem("Burger", 8.99, Category.MAIN, "images/burger.png");
    MenuItem item2 = new MenuItem("Burger", 12.50, Category.DESSERT, null);
    assertEquals(item1.hashCode(), item2.hashCode());
  }
}
