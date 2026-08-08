package menuapp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Tests for Inventory: stock rules, low-stock list, and sales/revenue totals. */
public class InventoryTest {

  /** setStock then getStock round-trips the value. */
  @Test
  void setAndGetStock() {
    Inventory inv = new Inventory();
    inv.setStock("Burger", 10);
    assertEquals(10, inv.getStock("Burger"));
  }

  /** An unknown item reads as zero stock. */
  @Test
  void getStockUnknownIsZero() {
    Inventory inv = new Inventory();
    assertEquals(0, inv.getStock("Ghost"));
  }

  /** Setting stock to zero is allowed. */
  @Test
  void setStockZeroAllowed() {
    Inventory inv = new Inventory();
    inv.setStock("Burger", 0);
    assertEquals(0, inv.getStock("Burger"));
  }

  /** Negative stock is rejected. */
  @Test
  void setStockNegativeThrows() {
    Inventory inv = new Inventory();
    assertThrows(IllegalArgumentException.class, () -> inv.setStock("Burger", -1));
  }

  /** isInStock is true only when stock is above zero. */
  @Test
  void isInStockReflectsStock() {
    Inventory inv = new Inventory();
    inv.setStock("Burger", 3);
    assertTrue(inv.isInStock("Burger"));
    inv.setStock("Burger", 0);
    assertFalse(inv.isInStock("Burger"));
    assertFalse(inv.isInStock("Ghost"));
  }

  /** increase adds to existing stock. */
  @Test
  void increaseAddsToStock() {
    Inventory inv = new Inventory();
    inv.setStock("Burger", 5);
    inv.increase("Burger", 3);
    assertEquals(8, inv.getStock("Burger"));
  }

  /** Increasing an unknown item creates it from zero. */
  @Test
  void increaseUnknownCreatesEntry() {
    Inventory inv = new Inventory();
    inv.increase("Fresh", 4);
    assertEquals(4, inv.getStock("Fresh"));
  }

  /** A non-positive increase amount is rejected. */
  @Test
  void increaseNonPositiveThrows() {
    Inventory inv = new Inventory();
    assertThrows(IllegalArgumentException.class, () -> inv.increase("Burger", 0));
    assertThrows(IllegalArgumentException.class, () -> inv.increase("Burger", -2));
  }

  /** decrease reduces existing stock. */
  @Test
  void decreaseReducesStock() {
    Inventory inv = new Inventory();
    inv.setStock("Burger", 10);
    inv.decrease("Burger", 4);
    assertEquals(6, inv.getStock("Burger"));
  }

  /** Decreasing by exactly the current stock reaches zero. */
  @Test
  void decreaseToExactlyZeroAllowed() {
    Inventory inv = new Inventory();
    inv.setStock("Burger", 5);
    inv.decrease("Burger", 5);
    assertEquals(0, inv.getStock("Burger"));
  }

  /** Decreasing by more than the current stock is rejected. */
  @Test
  void decreaseMoreThanStockThrows() {
    Inventory inv = new Inventory();
    inv.setStock("Burger", 3);
    assertThrows(IllegalArgumentException.class, () -> inv.decrease("Burger", 4));
  }

  /** A non-positive decrease amount is rejected. */
  @Test
  void decreaseNonPositiveThrows() {
    Inventory inv = new Inventory();
    inv.setStock("Burger", 3);
    assertThrows(IllegalArgumentException.class, () -> inv.decrease("Burger", 0));
  }

  /** Decreasing an unknown item (zero stock) by a positive amount is rejected. */
  @Test
  void decreaseUnknownThrows() {
    Inventory inv = new Inventory();
    assertThrows(IllegalArgumentException.class, () -> inv.decrease("Ghost", 1));
  }

  /** Low stock includes items at or below the threshold and excludes those above. */
  @Test
  void lowStockIncludesAtOrBelowThreshold() {
    Inventory inv = new Inventory();
    inv.setStock("A", 5);
    inv.setStock("B", 3);
    inv.setStock("C", 8);
    assertEquals(List.of("A", "B"), inv.lowStockItems(5));
  }

  /** A threshold of zero lists only items with zero stock. */
  @Test
  void lowStockThresholdZeroListsZeroStock() {
    Inventory inv = new Inventory();
    inv.setStock("A", 0);
    inv.setStock("B", 2);
    assertEquals(List.of("A"), inv.lowStockItems(0));
  }

  /** The low stock list comes back sorted by name. */
  @Test
  void lowStockResultIsSorted() {
    Inventory inv = new Inventory();
    inv.setStock("Zeta", 1);
    inv.setStock("Alpha", 1);
    inv.setStock("Mango", 1);
    assertEquals(List.of("Alpha", "Mango", "Zeta"), inv.lowStockItems(5));
  }

  /** Mutating the map from getAllStock does not change the inventory. */
  @Test
  void getAllStockReturnsDefensiveCopy() {
    Inventory inv = new Inventory();
    inv.setStock("Burger", 5);
    Map<String, Integer> snapshot = inv.getAllStock();
    snapshot.put("Burger", 999);
    assertEquals(5, inv.getStock("Burger"));
  }

  /** recordSale raises the order count and the total revenue. */
  @Test
  void recordSaleIncrementsOrderCountAndTotal() {
    Inventory inv = new Inventory();
    Order order = new Order();
    order.add(new MenuItem("Burger", 8.99, Category.MAIN, null), 2);
    inv.recordSale(order);
    assertEquals(1, inv.getOrderCount());
    assertEquals(17.98, inv.getTotalRevenue(), 1e-9);
  }

  /** recordSale accumulates count and revenue across multiple orders. */
  @Test
  void recordSaleAccumulatesAcrossOrders() {
    Inventory inv = new Inventory();
    Order first = new Order();
    first.add(new MenuItem("Burger", 8.99, Category.MAIN, null), 1);
    Order second = new Order();
    second.add(new MenuItem("Cake", 5.00, Category.DESSERT, null), 2);
    inv.recordSale(first);
    inv.recordSale(second);
    assertEquals(2, inv.getOrderCount());
    assertEquals(18.99, inv.getTotalRevenue(), 1e-9);
  }

  /** recordSale splits each line into its category's revenue. */
  @Test
  void recordSaleSplitsRevenueByCategory() {
    Inventory inv = new Inventory();
    Order order = new Order();
    order.add(new MenuItem("Burger", 8.99, Category.MAIN, null), 2);
    order.add(new MenuItem("Cake", 5.00, Category.DESSERT, null), 1);
    order.add(new MenuItem("Soda", 2.50, Category.BEVERAGE, null), 3);
    inv.recordSale(order);
    Map<Category, Double> revenue = inv.getRevenueByCategory();
    assertEquals(17.98, revenue.get(Category.MAIN), 1e-9);
    assertEquals(5.00, revenue.get(Category.DESSERT), 1e-9);
    assertEquals(7.50, revenue.get(Category.BEVERAGE), 1e-9);
  }

  /** Revenue by category starts with all three categories at zero. */
  @Test
  void revenueByCategoryHasAllCategoriesInitiallyZero() {
    Inventory inv = new Inventory();
    Map<Category, Double> revenue = inv.getRevenueByCategory();
    assertEquals(3, revenue.size());
    assertEquals(0.0, revenue.get(Category.MAIN), 1e-9);
    assertEquals(0.0, revenue.get(Category.DESSERT), 1e-9);
    assertEquals(0.0, revenue.get(Category.BEVERAGE), 1e-9);
  }

  /** An empty order still counts, but adds no revenue. */
  @Test
  void recordSaleEmptyOrderCountsButAddsNoRevenue() {
    Inventory inv = new Inventory();
    inv.recordSale(new Order());
    assertEquals(1, inv.getOrderCount());
    assertEquals(0.0, inv.getTotalRevenue(), 1e-9);
  }

  /** Mutating the map from getRevenueByCategory does not change the inventory. */
  @Test
  void getRevenueByCategoryReturnsDefensiveCopy() {
    Inventory inv = new Inventory();
    Order order = new Order();
    order.add(new MenuItem("Burger", 8.99, Category.MAIN, null), 1);
    inv.recordSale(order);
    Map<Category, Double> revenue = inv.getRevenueByCategory();
    revenue.put(Category.MAIN, 999.0);
    assertEquals(8.99, inv.getRevenueByCategory().get(Category.MAIN), 1e-9);
  }
}
