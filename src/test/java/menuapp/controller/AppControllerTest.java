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

// Tests for getters, searching, and filtering.

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
     * getLowStockItems includes every item at or below the threshold, sorted by name. 
     */
    @Test
    void getLowStockItemsReturnsNamesAtOrBelowThreshold() {
        List<String> lowStock = controller.getLowStockItems(10); //With threshold 10, Burger (10) and Cake (2) qualify; Cola (20) does not.
        assertEquals(List.of("Burger", "Cake"), lowStock);
    }

    /** getRevenueByCategory starts at zero for every category before any sale. */
    @Test
    void getRevenueByCategoryStartsAtZero() {
        Map<Category, Double> revenue = controller.getRevenueByCategory();
        assertEquals(0.0, revenue.get(Category.MAIN), 0.01);
        assertEquals(0.0, revenue.get(Category.DESSERT), 0.01);
        assertEquals(0.0, revenue.get(Category.BEVERAGE), 0.01);
    }



// Tests for cart operations: addToCart, removeFromCart, setCartQuantity, addToFavorites, restock

    /** addToCart adds one unit; adding the same item again merges into quantity two. */
    @Test
    void addItemToCartIncreasesQuantity() {
        MenuItem burger = new MenuItem("Burger", 8.99, Category.MAIN, null);
        controller.addToCart(burger);
        assertEquals(1, controller.getCart().size());
        assertEquals("Burger", controller.getCart().getItems().get(0).getName());
        assertEquals(1, controller.getCart().getItemsWithQuantities().get(burger).intValue());

        controller.addToCart(burger);
        assertEquals(1, controller.getCart().size());
        assertEquals(2, controller.getCart().getItemsWithQuantities().get(burger).intValue());
    }

    /** removeFromCart removes the whole cart line for that item name. */
    @Test
    void removeItemFromCartRemovesCartLine() {
        MenuItem burger = new MenuItem("Burger", 8.99, Category.MAIN, null);
        controller.addToCart(burger);
        controller.addToCart(burger);
        controller.removeFromCart("Burger");
        assertEquals(0, controller.getCart().size());
        assertTrue(controller.getCart().getItems().isEmpty());
    }

    /** setCartQuantity replaces the quantity of an item already in the cart. */
    @Test
    void setCartQuantitySetsQuantityOfItemInCart() {
        MenuItem burger = new MenuItem("Burger", 8.99, Category.MAIN, null);
        controller.addToCart(burger);
        controller.setCartQuantity("Burger", 3);
        assertEquals(1, controller.getCart().size());
        assertEquals(3, controller.getCart().getItemsWithQuantities().get(burger).intValue());
    }

    /** addToFavorites adds the item to the favorites list. */
    @Test
    void addToFavoritesAddsItemToFavorites() {
        controller.addToFavorites(new MenuItem("Burger", 8.99, Category.MAIN, null));
        assertEquals(1, controller.getFavorites().size());
        assertEquals("Burger", controller.getFavorites().getItems().get(0).getName());
    }

    /** restock increases the stock of the item. */
    @Test
    void restockIncreasesStockOfItem() {
        controller.restock("Burger", 5);
        assertEquals(15, controller.getInventory().getStock("Burger"));
    }
    
// Tests for checkout.



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
