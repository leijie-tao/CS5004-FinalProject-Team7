import static menuapp.model.Category.MAIN;
import static org.junit.jupiter.api.Assertions.*;

import menuapp.model.Category;
import menuapp.model.MenuItem;
import org.junit.jupiter.api.Test;

/** Tests for MenuItem. */
public class TestMenuItem {

    /** Test Getters of MenuItem. */
    @Test
    public void gettersReturnValue() {
        MenuItem item = new MenuItem("Salad", 14.49, Category.MAIN, "images/salad.png");
        assertEquals("Salad", item.getName());
        assertEquals(14.49, item.getPrice());
        assertEquals(Category.MAIN, item.getCategory());
        assertEquals("images/salad.png", item.getImagePath());
    }

    @Test
    void itemsWithSameName() {
        MenuItem item1 = new MenuItem("Burger", 8.99, Category.MAIN, "images/burger.png");
        MenuItem item2 = new MenuItem("Burger", 12.50, Category.DESSERT, null);
        assertEquals(item1, item2);
    }

    @Test
    void itemsWithDifferentNames() {
        MenuItem item1 = new MenuItem("Burger", 8.99, Category.MAIN, null);
        MenuItem item2 = new MenuItem("Fries", 8.99, Category.MAIN, null);
        assertNotEquals(item1, item2);
    }

    @Test
    void equalItemsHaveSameHashCode() {
        MenuItem item1 = new MenuItem("Burger", 8.99, Category.MAIN, "images/burger.png");
        MenuItem item2 = new MenuItem("Burger", 12.50, Category.DESSERT, null);
        assertEquals(item1.hashCode(), item2.hashCode());
    }
}
