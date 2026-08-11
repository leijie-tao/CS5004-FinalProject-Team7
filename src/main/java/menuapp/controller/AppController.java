package menuapp.controller;

import java.util.List;
import java.util.Map;

import menuapp.model.Category;
import menuapp.model.FavoritesList;
import menuapp.model.Inventory;
import menuapp.model.Menu;
import menuapp.model.MenuItem;
import menuapp.model.Order;
import menuapp.persistence.FileHandler;

/**
 * The single controller the view calls. It holds the catalog, inventory, and
 * persistence, plus the current cart and favorites, and exposes every action
 * the panels need.
 */
public class AppController {

  private final Menu menu;
  private final Inventory inventory;
  private final FileHandler fileHandler;
  private final Order cart;
  private FavoritesList favorites;

  /**
   * Wires the controller to the shared collaborators.
   *
   * @param menu the catalog
   * @param inventory the stock and the sales totals
   * @param fileHandler saves and loads files
   */
  public AppController(Menu menu, Inventory inventory, FileHandler fileHandler) {
    this.menu = menu;
    this.inventory = inventory;
    this.fileHandler = fileHandler;
    this.cart = new Order();
    this.favorites = new FavoritesList("favorites");
  }

  /** @return every item grouped by category, for the menu view */
  public Map<Category, List<MenuItem>> getGroupedMenu() {
    return menu.groupByCategory();
  }

  /**
   * Searches the menu by keyword.
   *
   * @param keyword the text to look for
   * @return the matching items
   */
  public List<MenuItem> search(String keyword) {
    return menu.search(keyword);
  }

  /**
   * Adds an item to the cart.
   *
   * @param item the item to add
   */
  public void addToCart(MenuItem item) {
    cart.add(item);
  }

  /**
   * Removes an item from the cart.
   *
   * @param name the name of the item to remove
   */
  public void removeFromCart(String name) {
    cart.remove(name);
  }

  /**
   * Sets the quantity of a cart item.
   *
   * @param name the name of the item
   * @param quantity the new quantity
   */
  public void setCartQuantity(String name, int quantity) {
    cart.setQuantity(name, quantity);
  }

  /** @return the current cart */
  public Order getCart() {
    return cart;
  }

  /**
   * Confirms the cart. Decreases inventory by each quantity, records the
   * sale, then clears the cart.
   */
  public void checkout() {
    for (Map.Entry<MenuItem, Integer> entry : cart.getItemsWithQuantities().entrySet()) {
      inventory.decrease(entry.getKey().getName(), entry.getValue());
    }
    inventory.recordSale(cart);
    cart.clear();
  }

  /**
   * Adds an item to the favorites list.
   *
   * @param item the item to add
   */
  public void addToFavorites(MenuItem item) {
    favorites.add(item);
  }

  /**
   * Filters the menu down to one category, for the live display.
   * This backs the optional filter feature.
   *
   * @param category the category to show
   * @return the items in that category
   */
  public List<MenuItem> filterByCategory(Category category) {
    return menu.itemsInCategory(category);
  }

  /** @return the current favorites list */
  public FavoritesList getFavorites() {
    return favorites;
  }

  /**
   * Saves the favorites list to a file.
   *
   * @param filePath where to write it
   */
  public void saveFavorites(String filePath) {
    fileHandler.save(favorites, filePath);
  }

  /**
   * Loads a favorites list from a file so it can be modified.
   *
   * @param filePath the file to read
   */
  public void loadFavorites(String filePath) {
    favorites = fileHandler.load(filePath, FavoritesList.class);
  }

  /**
   * Restocks an item from the staff screen.
   *
   * @param itemName the item to restock
   * @param amount how many units to add
   */
  public void restock(String itemName, int amount) {
    inventory.increase(itemName, amount);
  }

  /** @return the inventory, for the staff view */
  public Inventory getInventory() {
    return inventory;
  }

  /**
   * Builds the low stock sub-list at a chosen threshold. This is the
   * required build sub-list feature, with the threshold as the criterion.
   *
   * @param threshold the level to compare against
   * @return the names of low stock items
   */
  public List<String> getLowStockItems(int threshold) {
    return inventory.lowStockItems(threshold);
  }

  /**
   * Exports the low stock sub-list to a JSON file. This is the required
   * build sub-list plus save feature.
   *
   * @param threshold the low stock threshold
   * @param filePath where to write it
   */
  public void exportLowStock(int threshold, String filePath) {
    fileHandler.save(inventory.lowStockItems(threshold), filePath);
  }

  /**
   * Returns revenue by category, the data the sales chart draws.
   *
   * @return a map from category to its accumulated revenue
   */
  public Map<Category, Double> getRevenueByCategory() {
    return inventory.getRevenueByCategory();
  }
}
