package menuapp.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The current cart. Holds items with quantities and computes the total.
 * Shown in the order panel and recorded in sales at checkout.
 */
public class Order {

  /** Cart lines, item to quantity, kept in insertion order for a stable display. */
  private final Map<MenuItem, Integer> items = new LinkedHashMap<>();

  /**
   * Adds one unit of an item, or raises its quantity by one.
   *
   * @param item the item to add
   */
  public void add(MenuItem item) {
    add(item, 1);
  }

  /**
   * Adds a given quantity of an item, accumulating with any quantity already
   * in the cart.
   *
   * @param item the item to add
   * @param quantity how many units, must be positive
   * @throws IllegalArgumentException when quantity is not positive
   */
  public void add(MenuItem item, int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive: " + quantity);
    }
    items.merge(item, quantity, Integer::sum);
  }

  /**
   * Sets the quantity of an item already in the cart.
   *
   * @param name the name of the item
   * @param quantity the new quantity, must be positive
   * @throws IllegalArgumentException when quantity is not positive, or when no
   *     item with that name is in the cart
   */
  public void setQuantity(String name, int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive: " + quantity);
    }
    MenuItem key = findByName(name);
    if (key == null) {
      throw new IllegalArgumentException("Item not in cart: " + name);
    }
    items.put(key, quantity);
  }

  /**
   * Removes an item from the cart.
   *
   * @param name the name of the item to remove
   * @return true when the item was present
   */
  public boolean remove(String name) {
    MenuItem key = findByName(name);
    if (key == null) {
      return false;
    }
    items.remove(key);
    return true;
  }

  /** @return the distinct items in the cart, a fresh list in insertion order */
  public List<MenuItem> getItems() {
    return new ArrayList<>(items.keySet());
  }

  /** @return each item paired with its quantity, a fresh map in insertion order */
  public Map<MenuItem, Integer> getItemsWithQuantities() {
    return new LinkedHashMap<>(items);
  }

  /** @return the total price across all items and quantities */
  public double getTotal() {
    double total = 0.0;
    for (Map.Entry<MenuItem, Integer> entry : items.entrySet()) {
      total += entry.getKey().getPrice() * entry.getValue();
    }
    return total;
  }

  /** @return the number of distinct items */
  public int size() {
    return items.size();
  }

  /** Empties the cart. */
  public void clear() {
    items.clear();
  }

  /**
   * Finds the cart key whose name matches, or null when it is absent. The cart
   * is keyed by item, but callers reference lines by name, so the lookup walks
   * the keys and compares names.
   *
   * @param name the item name to look for
   * @return the matching key, or null when no line has that name
   */
  private MenuItem findByName(String name) {
    for (MenuItem item : items.keySet()) {
      if (item.getName().equals(name)) {
        return item;
      }
    }
    return null;
  }
}
