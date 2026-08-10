package menuapp.controller;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import menuapp.model.Category;
import menuapp.model.Inventory;
import menuapp.model.MenuItem;
import menuapp.model.RestaurantMenu;
import menuapp.persistence.FileHandler;

public class AppControllerTest {

  private AppController controller;

  @BeforeEach
  void setUp() {
    RestaurantMenu menu = new RestaurantMenu();
    menu.addItem(new MenuItem("Burger", 8.99, Category.MAIN, null));
    menu.addItem(new MenuItem("Cake", 5.50, Category.DESSERT, null));
    menu.addItem(new MenuItem("Cola", 2.00, Category.BEVERAGE, null));

    Inventory inventory = new Inventory();
    inventory.setStock("Burger", 10);
    inventory.setStock("Cake", 2);
    inventory.setStock("Cola", 20);

    controller = new AppController(menu, inventory, new NoOpFileHandler());
  }

  /** getGroupedMenu returns one section per category. */
  @Test
  void getGroupedMenuGroupsSeededItemsByCategory() {
    Map<Category, List<MenuItem>> grouped = controller.getGroupedMenu();
    assertEquals(3, grouped.size());
    assertEquals(1, grouped.get(Category.MAIN).size());
    assertEquals(1, grouped.get(Category.DESSERT).size());
    assertEquals(1, grouped.get(Category.BEVERAGE).size());
    assertEquals("Burger", grouped.get(Category.MAIN).get(0).getName());
  }

  /** search finds an item whose name contains the keyword. */
  @Test
  void searchFindsItemByKeyword() {
    List<MenuItem> found = controller.search("Burger");
    assertEquals(1, found.size());
    assertEquals("Burger", found.get(0).getName());
  }

  /** search returns an empty list when nothing matches. */
  @Test
  void searchReturnsEmptyWhenNoMatch() {
    assertTrue(controller.search("Pizza").isEmpty());
  }

  /** filterByCategory returns only items in that category. */
  @Test
  void filterByCategoryReturnsOnlyMatchingCategory() {
    List<MenuItem> filtered = controller.filterByCategory(Category.MAIN);
    assertEquals(1, filtered.size());
    assertEquals("Burger", filtered.get(0).getName());
  }

  /** getCart always returns the same cart instance. */
  @Test
  void getCartReturnsSameInstance() {
    assertSame(controller.getCart(), controller.getCart());
    assertEquals(0, controller.getCart().size());
  }

  /** getFavorites always returns the same favorites instance. */
  @Test
  void getFavoritesReturnsSameInstance() {
    assertSame(controller.getFavorites(), controller.getFavorites());
    assertEquals(0, controller.getFavorites().size());
  }

  /** getInventory returns the injected inventory with seeded stock. */
  @Test
  void getInventoryReturnsInjectedInventory() {
    assertSame(controller.getInventory(), controller.getInventory());
    assertEquals(10, controller.getInventory().getStock("Burger"));
    assertEquals(2, controller.getInventory().getStock("Cake"));
    assertEquals(20, controller.getInventory().getStock("Cola"));
  }

  /**
   * getLowStockItems includes every item at or below the threshold, sorted by
   * name. With threshold 10, Burger (10) and Cake (2) qualify; Cola (20) does not.
   */
  @Test
  void getLowStockItemsReturnsNamesAtOrBelowThreshold() {
    List<String> lowStock = controller.getLowStockItems(10);
    assertEquals(List.of("Burger", "Cake"), lowStock);
  }

  /** getRevenueByCategory starts at zero for every category before any sale. */
  @Test
  void getRevenueByCategoryStartsAtZero() {
    Map<Category, Double> revenue = controller.getRevenueByCategory();
    assertEquals(0.0, revenue.get(Category.MAIN), 0.0001);
    assertEquals(0.0, revenue.get(Category.DESSERT), 0.0001);
    assertEquals(0.0, revenue.get(Category.BEVERAGE), 0.0001);
  }




  /**
   * Minimal FileHandler for tests that do not exercise save/load yet.
   */
  private static class NoOpFileHandler implements FileHandler {

    @Override
    public <T> void save(T data, String filePath) {
    }

    @Override
    public <T> T load(String filePath, Class<T> type) {
      return null;
    }
  }
}
