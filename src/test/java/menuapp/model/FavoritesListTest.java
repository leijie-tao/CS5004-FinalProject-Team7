package menuapp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for FavoritesList.
 *
 * <p>Restored from the original {@code TestFavoriteList.java}, which was
 * accidentally removed when PR #6 ("Situ model customers") was reverted
 * (commit 7a1a0fa). Recovered via {@code git show 7a1a0fa^:...} and moved
 * into the current {@code menuapp.model} test package to match the project's
 * updated test layout.
 *
 * <p>{@code addNoSubstringFalsePositive()} below is a new addition, not part
 * of the original file. It is a regression test for a bug found after this
 * file was lost: {@code contains(String)} originally used
 * {@code String.contains()} (substring match) instead of {@code equals()}
 * (exact match), so adding "Coffee" was silently rejected whenever
 * "Iced Coffee" was already in the list. The original suite only tested
 * exact-name duplicates (e.g. "Cake" vs "Cake"), which passes under both the
 * buggy and the fixed implementation and would not have caught this case.
 */
public class FavoritesListTest {

  private FavoritesList favorite;
  private MenuItem cake;
  private MenuItem salad;
  private MenuItem coke;
  private MenuItem burger;

  /** Creates a fresh, empty favorites list and four reusable items before each test. */
  @BeforeEach
  void setUp() {
    favorite = new FavoritesList("My Favorites");
    cake = new MenuItem("Cake", 6.99, Category.DESSERT, null);
    salad = new MenuItem("Salad", 14.99, Category.MAIN, null);
    coke = new MenuItem("Coke", 2.99, Category.BEVERAGE, null);
    burger = new MenuItem("Burger", 12.99, Category.MAIN, null);
  }

  /** Verifies a newly created list starts empty. */
  @Test
  void newListStartsEmpty() {
    assertEquals(0, favorite.size());
  }

  /** Verifies adding an item increases the list size and includes that item. */
  @Test
  void addItemTest() {
    favorite.add(burger);
    favorite.add(cake);
    favorite.add(salad);
    favorite.add(coke);

    assertEquals(4, favorite.size());
    assertTrue(favorite.contains("Cake"));
    assertTrue(favorite.contains("Coke"));
    assertTrue(favorite.contains("Burger"));
    assertTrue(favorite.contains("Salad"));
  }

  /** Verifies adding an item with a name already present does not create a duplicate. */
  @Test
  void addNotDuplicateExistingItem() {
    favorite.add(cake);
    favorite.add(new MenuItem("Cake", 99.99, Category.MAIN, null));
    assertEquals(1, favorite.size());
  }

  /**
   * Regression test: an item whose name is merely a substring of an
   * existing entry (or vice versa) must NOT be treated as a duplicate.
   * "Coffee" and "Iced Coffee" are different items and both must be
   * addable. This is the exact case that slipped past the original suite.
   */
  @Test
  void addNoSubstringFalsePositive() {
    MenuItem icedCoffee = new MenuItem("Iced Coffee", 4.50, Category.BEVERAGE, null);
    MenuItem coffee = new MenuItem("Coffee", 3.50, Category.BEVERAGE, null);

    favorite.add(icedCoffee);
    favorite.add(coffee);

    assertEquals(2, favorite.size());
    assertTrue(favorite.contains("Coffee"));
    assertTrue(favorite.contains("Iced Coffee"));
  }

  /** Verifies addAll adds every item in the given batch. */
  @Test
  void addAllAddsEveryItemInBatch() {
    favorite.addAll(Arrays.asList(cake, salad, coke, burger));

    assertEquals(4, favorite.size());
    assertTrue(favorite.contains("Cake"));
    assertTrue(favorite.contains("Salad"));
    assertTrue(favorite.contains("Coke"));
    assertTrue(favorite.contains("Burger"));
  }

  /** Verifies removing an existing item deletes it and shrinks the list. */
  @Test
  void removeItemTest() {
    favorite.add(cake);

    boolean removed = favorite.remove("Cake");

    assertTrue(removed);
    assertEquals(0, favorite.size());
    assertFalse(favorite.contains("Cake"));
  }

  /** Verifies removing a name that is not in the list returns false and changes nothing. */
  @Test
  void removeNameNotFound() {
    favorite.add(cake);

    boolean removed = favorite.remove("Nonexistent");

    assertFalse(removed);
    assertEquals(1, favorite.size());
  }

  /** Verifies contains returns true only for items that were added. */
  @Test
  void containsTest() {
    favorite.add(cake);

    assertTrue(favorite.contains("Cake"));
    assertFalse(favorite.contains("Salad"));
  }

  /** Verifies getItems returns every item currently in the list. */
  @Test
  void getItemsTest() {
    favorite.add(cake);
    favorite.add(salad);

    List<MenuItem> items = favorite.getItems();

    assertEquals(2, items.size());
    assertTrue(items.contains(cake));
    assertTrue(items.contains(salad));
  }

  /** Verifies getName returns the name given at construction. */
  @Test
  void getNameTest() {
    assertEquals("My Favorites", favorite.getName());
  }

  /** Verifies setName changes the list's name. */
  @Test
  void setNameTest() {
    favorite.setName("Weekend Picks");
    assertEquals("Weekend Picks", favorite.getName());
  }
}
