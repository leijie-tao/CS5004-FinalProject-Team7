package menuapp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for RestaurantMenu.
 *
 * <p>Restored from the original {@code TestRestaurantMenu.java}, which was
 * accidentally removed when PR #6 ("Situ model customers") was reverted
 * (commit 7a1a0fa). Recovered via {@code git show 7a1a0fa^:...} and moved
 * into the current {@code menuapp.model} test package to match the project's
 * updated test layout.
 *
 * <p>Note: {@code sortedByOrder()} below is the only existing coverage for
 * {@link Menu#sortedBy}. As of this restoration, no caller in
 * {@code AppController} or the view layer invokes {@code sortedBy} yet, so
 * this test protects the model-level contract but does not confirm the
 * feature is wired end-to-end.
 */
public class RestaurantMenuTest {

  private RestaurantMenu menu;
  private MenuItem cake;
  private MenuItem salad;
  private MenuItem coke;
  private MenuItem burger;

  /** Creates a fresh menu with four items before each test. */
  @BeforeEach
  void setUp() {
    menu = new RestaurantMenu();
    cake = new MenuItem("Cake", 6.99, Category.DESSERT, null);
    salad = new MenuItem("Salad", 14.99, Category.MAIN, null);
    coke = new MenuItem("Coke", 2.99, Category.BEVERAGE, null);
    burger = new MenuItem("Burger", 12.99, Category.MAIN, null);

    menu.addItem(burger);
    menu.addItem(cake);
    menu.addItem(salad);
    menu.addItem(coke);
  }

  /** Verifies the menu reports the correct number of items after setup. */
  @Test
  void itemSize() {
    assertEquals(4, menu.size());
  }

  /** Verifies adding a new item increases the menu size by one. */
  @Test
  void addItemIncreaseSize() {
    menu.addItem(new MenuItem("coffee", 4.99, Category.BEVERAGE, null));
    assertEquals(5, menu.size());
  }

  /** Verifies removing an existing item deletes it and shrinks the menu. */
  @Test
  void removeItemDeletesMatchingItem() {
    boolean removed = menu.removeItem("Burger");

    assertTrue(removed);
    assertEquals(3, menu.size());
  }

  /** Verifies removing a name that is not on the menu returns false and changes nothing. */
  @Test
  void removeItemReturnsFalseWhenNameNotFound() {
    boolean removed = menu.removeItem("Nonexistent");

    assertFalse(removed);
    assertEquals(4, menu.size());
  }

  /** Verifies getAllItems returns every item currently on the menu. */
  @Test
  void getAllItemsReturnsEveryItem() {
    List<MenuItem> items = menu.getAllItems();

    assertEquals(4, items.size());
    assertTrue(items.contains(burger));
    assertTrue(items.contains(coke));
    assertTrue(items.contains(cake));
    assertTrue(items.contains(salad));
  }

  /**
   * Verifies getAllItems returns a fresh copy, so modifying the returned
   * list does not affect the menu's internal data.
   */
  @Test
  void getAllItemsReturnsCopyNotInternalReference() {
    List<MenuItem> items = menu.getAllItems();
    items.clear();

    // Modifying the returned list must not affect the menu itself.
    assertEquals(4, menu.size());
  }

  /** Verifies groupByCategory places each item under its correct category. */
  @Test
  void groupByCategory() {
    Map<Category, List<MenuItem>> group = menu.groupByCategory();
    assertEquals(2, group.get(Category.MAIN).size());
    assertTrue(group.get(Category.MAIN).contains(burger));
    assertTrue(group.get(Category.BEVERAGE).contains(coke));
    assertTrue(group.get(Category.DESSERT).contains(cake));
  }

  /**
   * Verifies sortedBy orders items according to the given comparator.
   * See the class-level note: this is currently the only test covering
   * this method, and it is not yet called from AppController or the view.
   */
  @Test
  void sortedByOrder() {
    List<MenuItem> sorted = menu.sortedBy(Comparator.comparingDouble(MenuItem::getPrice));
    assertEquals("Coke", sorted.get(0).getName());
    assertEquals("Cake", sorted.get(1).getName());
    assertEquals("Burger", sorted.get(2).getName());
    assertEquals("Salad", sorted.get(3).getName());
  }

  /** Verifies search finds items whose name contains the given keyword. */
  @Test
  void searchContainingKeyword() {
    List<MenuItem> results = menu.search("urg");
    assertEquals(1, results.size());
    assertTrue(results.contains(burger));
  }

  /** Verifies search returns an empty list when no item matches the keyword. */
  @Test
  void searchReturnsEmptyListWhenNoMatch() {
    List<MenuItem> results = menu.search("Sushi");

    assertTrue(results.isEmpty());
  }

  /** Verifies itemsInCategory returns only the items belonging to that category. */
  @Test
  void itemsInCategoryReturnsOnlyMatchingItems() {
    List<MenuItem> mains = menu.itemsInCategory(Category.MAIN);

    assertEquals(2, mains.size());
    assertTrue(mains.contains(burger));
    assertTrue(mains.contains(salad));
  }
}
