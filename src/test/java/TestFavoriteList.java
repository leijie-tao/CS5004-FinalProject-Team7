import menuapp.model.Category;
import menuapp.model.FavoritesList;
import menuapp.model.MenuItem;
import menuapp.model.RestaurantMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestFavoriteList {
    private FavoritesList favorite;
    private MenuItem cake;
    private MenuItem salad;
    private MenuItem coke;
    private MenuItem burger;


    /** Creates a fresh menu with four items before each test. */
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
