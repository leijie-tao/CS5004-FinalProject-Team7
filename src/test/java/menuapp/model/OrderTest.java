package menuapp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Tests for the Order cart: quantities, totals, lookups, and defensive copies. */
public class OrderTest {

  /** Adding one unit of a new item starts its quantity at one. */
  @Test
  void addOneUnitStartsAtQuantityOne() {
    Order order = new Order();
    MenuItem burger = new MenuItem("Burger", 8.99, Category.MAIN, null);
    order.add(burger);
    assertEquals(1, order.size());
    assertEquals(1, order.getItemsWithQuantities().get(burger).intValue());
  }

  /** Adding the same item twice merges into one line with quantity two. */
  @Test
  void addMergesDuplicateIntoOneLine() {
    Order order = new Order();
    MenuItem burger = new MenuItem("Burger", 8.99, Category.MAIN, null);
    order.add(burger);
    order.add(burger);
    assertEquals(1, order.size());
    assertEquals(2, order.getItemsWithQuantities().get(burger).intValue());
  }

  /** Two-arg add accumulates onto any existing quantity. */
  @Test
  void addWithQuantityAccumulates() {
    Order order = new Order();
    MenuItem burger = new MenuItem("Burger", 8.99, Category.MAIN, null);
    order.add(burger, 2);
    order.add(burger, 3);
    assertEquals(1, order.size());
    assertEquals(5, order.getItemsWithQuantities().get(burger).intValue());
  }

  /** A non-positive quantity on add is rejected. */
  @Test
  void addWithNonPositiveQuantityThrows() {
    Order order = new Order();
    MenuItem burger = new MenuItem("Burger", 8.99, Category.MAIN, null);
    assertThrows(IllegalArgumentException.class, () -> order.add(burger, 0));
    assertThrows(IllegalArgumentException.class, () -> order.add(burger, -1));
  }

  /** Adding a null item is rejected. */
  @Test
  void addNullItemThrows() {
    Order order = new Order();
    assertThrows(IllegalArgumentException.class, () -> order.add(null));
  }

  /** setQuantity replaces the quantity of an item already in the cart. */
  @Test
  void setQuantityUpdatesExistingItem() {
    Order order = new Order();
    MenuItem burger = new MenuItem("Burger", 8.99, Category.MAIN, null);
    order.add(burger, 1);
    order.setQuantity("Burger", 4);
    assertEquals(4, order.getItemsWithQuantities().get(burger).intValue());
  }

  /** setQuantity rejects a non-positive quantity. */
  @Test
  void setQuantityNonPositiveThrows() {
    Order order = new Order();
    MenuItem burger = new MenuItem("Burger", 8.99, Category.MAIN, null);
    order.add(burger, 1);
    assertThrows(IllegalArgumentException.class, () -> order.setQuantity("Burger", 0));
  }

  /** setQuantity rejects an item that is not in the cart. */
  @Test
  void setQuantityAbsentItemThrows() {
    Order order = new Order();
    assertThrows(IllegalArgumentException.class, () -> order.setQuantity("Ghost", 2));
  }

  /** Removing a present item returns true and empties the line. */
  @Test
  void removePresentReturnsTrue() {
    Order order = new Order();
    MenuItem burger = new MenuItem("Burger", 8.99, Category.MAIN, null);
    order.add(burger, 1);
    assertTrue(order.remove("Burger"));
    assertEquals(0, order.size());
  }

  /** Removing an absent item returns false. */
  @Test
  void removeAbsentReturnsFalse() {
    Order order = new Order();
    assertFalse(order.remove("Ghost"));
  }

  /** The total is the sum of price times quantity over all lines. */
  @Test
  void getTotalSumsPriceTimesQuantity() {
    Order order = new Order();
    MenuItem burger = new MenuItem("Burger", 8.99, Category.MAIN, null);
    MenuItem cake = new MenuItem("Cake", 5.00, Category.DESSERT, null);
    order.add(burger, 2);
    order.add(cake, 1);
    assertEquals(22.98, order.getTotal(), 1e-9);
  }

  /** An empty cart totals zero. */
  @Test
  void getTotalEmptyCartIsZero() {
    Order order = new Order();
    assertEquals(0.0, order.getTotal(), 1e-9);
  }

  /** getItems returns the items in insertion order. */
  @Test
  void getItemsPreservesInsertionOrder() {
    Order order = new Order();
    MenuItem burger = new MenuItem("Burger", 8.99, Category.MAIN, null);
    MenuItem cake = new MenuItem("Cake", 5.00, Category.DESSERT, null);
    MenuItem soda = new MenuItem("Soda", 2.50, Category.BEVERAGE, null);
    order.add(burger);
    order.add(cake);
    order.add(soda);
    assertEquals(List.of(burger, cake, soda), order.getItems());
  }

  /** Mutating the list from getItems does not change the cart. */
  @Test
  void getItemsReturnsDefensiveCopy() {
    Order order = new Order();
    MenuItem burger = new MenuItem("Burger", 8.99, Category.MAIN, null);
    order.add(burger);
    List<MenuItem> items = order.getItems();
    items.clear();
    assertEquals(1, order.size());
  }

  /** Mutating the map from getItemsWithQuantities does not change the cart. */
  @Test
  void getItemsWithQuantitiesReturnsDefensiveCopy() {
    Order order = new Order();
    MenuItem burger = new MenuItem("Burger", 8.99, Category.MAIN, null);
    order.add(burger, 1);
    Map<MenuItem, Integer> copy = order.getItemsWithQuantities();
    copy.put(burger, 99);
    assertEquals(1, order.getItemsWithQuantities().get(burger).intValue());
  }

  /** clear empties the cart, its total, and its item list. */
  @Test
  void clearEmptiesCart() {
    Order order = new Order();
    MenuItem burger = new MenuItem("Burger", 8.99, Category.MAIN, null);
    MenuItem cake = new MenuItem("Cake", 5.00, Category.DESSERT, null);
    order.add(burger, 2);
    order.add(cake, 1);
    order.clear();
    assertEquals(0, order.size());
    assertEquals(0.0, order.getTotal(), 1e-9);
    assertTrue(order.getItems().isEmpty());
  }
}
