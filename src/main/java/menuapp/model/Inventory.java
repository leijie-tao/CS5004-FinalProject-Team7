package menuapp.model;

import java.util.List;
import java.util.Map;

/**
 * Tracks how many units of each item are in stock, and the running sales
 * totals. Used by the staff screen. Keyed by item name, so every key must match
 * a {@code Menu} item name exactly.
 */
public class Inventory {

  /**
   * Sets the stock level of an item.
   *
   * @param itemName the name of the item
   * @param quantity the stock level, must not be negative
   */
  public void setStock(String itemName, int quantity) {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * Reads the stock level of an item.
   *
   * @param itemName the name of the item
   * @return the current stock, or zero when unknown
   */
  public int getStock(String itemName) {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * Tests whether an item has any stock.
   *
   * @param itemName the name of the item
   * @return true when stock is above zero
   */
  public boolean isInStock(String itemName) {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * Increases the stock of an item, used when staff restock.
   *
   * @param itemName the name of the item
   * @param amount how many units to add, must be positive
   */
  public void increase(String itemName, int amount) {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * Reduces the stock of an item, used when an order is placed.
   *
   * @param itemName the name of the item
   * @param amount how many units to remove, must be positive
   */
  public void decrease(String itemName, int amount) {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * Lists items at or below a stock threshold, the ones needing restock.
   * This backs the required build sub-list feature on the staff screen.
   *
   * @param threshold the level to compare against
   * @return the names of low stock items
   */
  public List<String> lowStockItems(int threshold) {
    throw new UnsupportedOperationException("TODO");
  }

  /** @return a snapshot of every item name and its stock, for the inventory table */
  public Map<String, Integer> getAllStock() {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * Records one placed order. Raises the order count, adds the order total to
   * revenue, and adds each item line to the revenue for its category. Called
   * by the controller at checkout.
   *
   * @param order the order that was placed
   */
  public void recordSale(Order order) {
    throw new UnsupportedOperationException("TODO");
  }

  /** @return how many orders have been placed */
  public int getOrderCount() {
    throw new UnsupportedOperationException("TODO");
  }

  /** @return the total revenue across all placed orders */
  public double getTotalRevenue() {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * Returns revenue split by category, the data behind the sales chart.
   *
   * @return a new map from category to its accumulated revenue
   */
  public Map<Category, Double> getRevenueByCategory() {
    throw new UnsupportedOperationException("TODO");
  }
}