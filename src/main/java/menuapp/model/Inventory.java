package menuapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks how many units of each item are in stock, and the running sales
 * totals. Used by the staff screen. Keyed by item name, so every key must match
 * a {@code Menu} item name exactly.
 */
public class Inventory {

  /** Stock level per item name; a missing name reads as zero. */
  private final Map<String, Integer> stock = new HashMap<>();

  /** Accumulated revenue per category; every category is seeded to zero. */
  private final Map<Category, Double> revenue = new EnumMap<>(Category.class);

  /** How many orders have been recorded. */
  private int orderCount;

  /** Running total revenue across all recorded orders. */
  private double totalRevenue;

  /** Creates an inventory with zero stock and every category revenue at zero. */
  public Inventory() {
    for (Category category : Category.values()) {
      revenue.put(category, 0.0);
    }
  }

  /**
   * Sets the stock level of an item.
   *
   * @param itemName the name of the item
   * @param quantity the stock level, must not be negative
   * @throws IllegalArgumentException when quantity is negative
   */
  public void setStock(String itemName, int quantity) {
    if (quantity < 0) {
      throw new IllegalArgumentException("Stock must not be negative: " + quantity);
    }
    stock.put(itemName, quantity);
  }

  /**
   * Reads the stock level of an item.
   *
   * @param itemName the name of the item
   * @return the current stock, or zero when unknown
   */
  public int getStock(String itemName) {
    return stock.getOrDefault(itemName, 0);
  }

  /**
   * Tests whether an item has any stock.
   *
   * @param itemName the name of the item
   * @return true when stock is above zero
   */
  public boolean isInStock(String itemName) {
    return getStock(itemName) > 0;
  }

  /**
   * Increases the stock of an item, used when staff restock. An unknown item is
   * treated as zero stock, so increasing it creates the entry.
   *
   * @param itemName the name of the item
   * @param amount how many units to add, must be positive
   * @throws IllegalArgumentException when amount is not positive
   */
  public void increase(String itemName, int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be positive: " + amount);
    }
    stock.put(itemName, getStock(itemName) + amount);
  }

  /**
   * Reduces the stock of an item, used when an order is placed. Stock can reach
   * zero but never goes negative.
   *
   * @param itemName the name of the item
   * @param amount how many units to remove, must be positive
   * @throws IllegalArgumentException when amount is not positive, or when amount
   *     exceeds the current stock
   */
  public void decrease(String itemName, int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be positive: " + amount);
    }
    int current = getStock(itemName);
    if (amount > current) {
      throw new IllegalArgumentException(
              "Cannot remove " + amount + " from stock of " + current);
    }
    stock.put(itemName, current - amount);
  }

  /**
   * Lists items at or below a stock threshold, the ones needing restock.
   * This backs the required build sub-list feature on the staff screen. Only
   * items with a stock entry are considered, and the names come back sorted so
   * the result is stable for display and export.
   *
   * @param threshold the level to compare against
   * @return the names of low stock items, in name order
   */
  public List<String> lowStockItems(int threshold) {
    List<String> result = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : stock.entrySet()) {
      if (entry.getValue() <= threshold) {
        result.add(entry.getKey());
      }
    }
    Collections.sort(result);
    return result;
  }

  /** @return a snapshot of every item name and its stock, for the inventory table */
  public Map<String, Integer> getAllStock() {
    return new HashMap<>(stock);
  }

  /**
   * Records one placed order. Raises the order count, adds the order total to
   * revenue, and adds each item line to the revenue for its category. An empty
   * order still counts as one placed order. Called by the controller at
   * checkout.
   *
   * @param order the order that was placed
   */
  public void recordSale(Order order) {
    orderCount++;
    totalRevenue += order.getTotal();
    for (Map.Entry<MenuItem, Integer> entry : order.getItemsWithQuantities().entrySet()) {
      MenuItem item = entry.getKey();
      int quantity = entry.getValue();
      Category category = item.getCategory();
      revenue.put(category, revenue.get(category) + item.getPrice() * quantity);
    }
  }

  /** @return how many orders have been placed */
  public int getOrderCount() {
    return orderCount;
  }

  /** @return the total revenue across all placed orders */
  public double getTotalRevenue() {
    return totalRevenue;
  }

  /**
   * Returns revenue split by category, the data behind the sales chart. Every
   * category is present, defaulting to zero.
   *
   * @return a new map from category to its accumulated revenue
   */
  public Map<Category, Double> getRevenueByCategory() {
    return new EnumMap<>(revenue);
  }
}