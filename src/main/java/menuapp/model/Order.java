package menuapp.model;

import java.util.List;
import java.util.Map;

/**
 * The current cart. Holds items with quantities and computes the total.
 * Shown in the order panel and recorded in sales at checkout.
 */
public class Order {

  /**
   * Adds one unit of an item, or raises its quantity by one.
   *
   * @param item the item to add
   */
  public void add(MenuItem item) {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * Adds a given quantity of an item.
   *
   * @param item the item to add
   * @param quantity how many units, must be positive
   */
  public void add(MenuItem item, int quantity) {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * Sets the quantity of an item already in the cart.
   *
   * @param name the name of the item
   * @param quantity the new quantity, must be positive
   */
  public void setQuantity(String name, int quantity) {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * Removes an item from the cart.
   *
   * @param name the name of the item to remove
   * @return true when the item was present
   */
  public boolean remove(String name) {
    throw new UnsupportedOperationException("TODO");
  }

  /** @return the distinct items in the cart */
  public List<MenuItem> getItems() {
    throw new UnsupportedOperationException("TODO");
  }

  /** @return each item paired with its quantity */
  public Map<MenuItem, Integer> getItemsWithQuantities() {
    throw new UnsupportedOperationException("TODO");
  }

  /** @return the total price across all items and quantities */
  public double getTotal() {
    throw new UnsupportedOperationException("TODO");
  }

  /** @return the number of distinct items */
  public int size() {
    throw new UnsupportedOperationException("TODO");
  }

  /** Empties the cart. */
  public void clear() {
    throw new UnsupportedOperationException("TODO");
  }
}