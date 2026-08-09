package menuapp.testsupport;

import menuapp.controller.AppController;
import menuapp.model.Category;
import menuapp.model.FavoritesList;
import menuapp.model.MenuItem;
import menuapp.model.Order;

import java.util.*;

/**
 * A stand-in {@link AppController} that lets the favorites screen be built and
 * run before the real controller exists.
 * At integration time nothing in {@code FavoritesPanel} changes. You simply
 * pass the real {@code AppController} into the constructor instead of this one.
 * V2: Added
 */
public class
MockController extends AppController {

  /** The list this fake hands back, pre-loaded so the table is never empty. */
  private MockFavoritesList favorites;
  /** The last path passed to save or load, handy for eyeballing behavior. */
  private String lastFilePath;
  /** Catalog for our mock controller since there's no real menu yet. This is hard coded menu. */
  private final List<MenuItem> menuItems = new ArrayList<MenuItem>();
  /** Items handed to addToCart(MenuItem) to be added and organized in call order */
  private final List<MenuItem> cartAdds = new ArrayList<MenuItem>();
  /** Items handed to addToFavorites(MenuItem) to be added organized in call order */
  private final List<MenuItem> favoriteAdds = new ArrayList<MenuItem>();
  /** The cart this fake owns. {@code AppController} builds one too, but keeps it
   * in a private field behind a {@code getCart} that still throws. */
  private final Order cart = new Order();
  /** Stock per item name, so checkout can refuse a line the way */
  private final Map<String, Integer> stock = new HashMap<String, Integer>();
  /** Stock every item */
  private static final int DEFAULT_STOCK = 20;
  /** Checkout refusal is reachable by pressing plus a few times rather than by editing this file. */
  private static final String SCARCE_ITEM = "Durian Ice Cream";
  /** Two units of {@link #SCARCE_ITEM} exist and any more than than two would not work. */
  private static final int SCARCE_STOCK = 2;
  /** How many times checkout has completed. */
  private int checkoutCount;
  /** The cart total at the last completed checkout. */
  private double lastCheckoutTotal;


  /** Creates a fake controller holding a small seeded favorites list. */
  public MockController() {
    super(null, null, null);
    this.favorites = new MockFavoritesList("My Favorites");
    mockSeedMenu();
    seedStartingItems();
  }

  /**
   * Catalog with items spread amongst (5) main, (5) dessert, and (5) beverage to make sure filters work.
   * Menu items purposely picked to test against UTF rendering.
   * Throws "Sold Out Special" with items inventory low.
   */
  private void mockSeedMenu() {
    // Main
    menuItems.add(new MenuItem("Côte de bœuf", 33.00, Category.MAIN, null));
    menuItems.add(new MenuItem("Cazuela de castañas", 29.50, Category.MAIN, null));
    menuItems.add(new MenuItem("Pečená kachna", 31.00, Category.MAIN, null));
    menuItems.add(new MenuItem("Bún Thịt Nướng", 30.135, Category.MAIN,null));
    menuItems.add(new MenuItem("Cumin Lamb & Biang-Biang Noodles",
            25.5, Category.MAIN,null));
    // Dessert
    menuItems.add(new MenuItem("Savoureux", 7.00, Category.DESSERT,null));
    menuItems.add(new MenuItem("Piñonates", 5.5, Category.DESSERT, null));
    menuItems.add(new MenuItem("Štrúdl", 6.601, Category.DESSERT, null));
    menuItems.add(new MenuItem("Chè Bắp", 4.5, Category.DESSERT,null));
    menuItems.add(new MenuItem("Durian Ice Cream", 3.5, Category.DESSERT,null));
    // Beverage
    menuItems.add(new MenuItem("Café au lait", 3.50, Category.BEVERAGE, null));
    menuItems.add(new MenuItem("Cortado con canela", 3.00, Category.BEVERAGE, null));
    menuItems.add(new MenuItem("Alžírská káva", 4.56, Category.BEVERAGE, null));
    menuItems.add(new MenuItem("Cà Phê Sữa Đá", 3.75, Category.BEVERAGE, null));
    menuItems.add(new MenuItem("Yuenyeung", 4,Category.BEVERAGE, null));
  }

  /** Gives every catalog item a stock level, then knocks one down so the checkout refusal has something to refuse. */
  private void mockSeedStock() {
    for (MenuItem item : menuItems) {
      stock.put(item.getName(), DEFAULT_STOCK);
    }
    stock.put(SCARCE_ITEM, SCARCE_STOCK);
  }

  /** Fills the list with realistic sample items.*/
  private void seedStartingItems() {
    favorites.add(new MenuItem("Côte de bœuf", 33.00, Category.MAIN, null));
    favorites.add(new MenuItem("Cazuela de castañas", 29.50, Category.MAIN, null));
    favorites.add(new MenuItem("Štrúdl", 6.601, Category.DESSERT, null));
    favorites.add(new MenuItem("Cortado con canela", 3.00, Category.BEVERAGE, null));
  }


  /**
   * Catalog grouped by category. There is a category holding no items that would be left out of the map
   * to model after {@code RestaurantMenu.groupByCategory} behavior.
   * @return new map from category with items
   */
  @Override
  public Map<Category, List<MenuItem>> getGroupedMenu() {
    Map<Category, List<MenuItem>> grouped =
            new LinkedHashMap<Category, List<MenuItem>>();
    for (MenuItem item : menuItems) {
      List<MenuItem> itemsInCategory = grouped.get(item.getCategory());
      if (itemsInCategory == null) {
        itemsInCategory = new ArrayList<MenuItem>();
        grouped.put(item.getCategory(), itemsInCategory);
      }
      itemsInCategory.add(item);
    }
    return grouped;
  }

  /**
   * Searches for items with keywords while ignoring case.
   * @param keyword the text to look for
   * @return new list of matching items. This list is empty if nothing matches.
   */
  @Override
  public List<MenuItem> search(String keyword) {
    List<MenuItem> matches = new ArrayList<MenuItem>();
    if (keyword == null) {
      return matches;
    }
    String needle = keyword.toLowerCase(Locale.US);
    for (MenuItem item : menuItems) {
      if (item.getName().toLowerCase(Locale.US).contains(needle)) {
        matches.add(item);
      }
    }
    return matches;
  }

  /**
   * This returns an item from a category.
   * @param category the category to show
   * @return new list of items from selected category
   */
  @Override
  public List<MenuItem> filterByCategory(Category category) {
    List<MenuItem> inCategory = new ArrayList<MenuItem>();
    for (MenuItem item : menuItems) {
      if (item.getCategory() == category) {
        inCategory.add(item);
      }
    }
    return inCategory;
  }

  /** Item fake controller refuses. This is only to make sure error panel shows up. */
  private static final String UNAVAILABLE_ITEM = "Pečená kachna";

  /**
   * Item addeed to cart is recorded while items not available are not.
   * @param item to add
   * @throws RuntimeException when item is sold out
   */
  @Override
  public void addToCart(MenuItem item) {
    if (item == null) {
      return;
    }
    if (UNAVAILABLE_ITEM.equals(item.getName())) {
      throw new RuntimeException("Mock refusal: " + item.getName() + " is unavailable");
    }
    cartAdds.add(item);
    cart.add(item);
    System.out.println("MockController: added " + item.getName() + " to the cart");
  }

  /** @return the name of the item this fake refuses, for the demo and for tests */
  public static String getUnavailableItemName() {
    return UNAVAILABLE_ITEM;
  }

  /** @return the seeded favorites list */
  @Override
  public FavoritesList getFavorites() {
    return favorites;
  }

  /**
   * Adds an item to the favorites list.
   * @param item the item to add
   */
  @Override
  public void addToFavorites(MenuItem item) {
    favoriteAdds.add(item);
    favorites.add(item);
  }

  /**
   * Mock writes the list to disk with any word that is "bad" is thrown instead. 
   * Panel's error dialog can be handled without touching the file system.
   * @param filePath where the list would be written
   */
  @Override
  public void saveFavorites(String filePath) {
    if (filePath != null && filePath.contains("bad")) {
      throw new RuntimeException("Mock write failure for " + filePath);
    }
    this.lastFilePath = filePath;
    System.out.println("MockController: saved " + favorites.size()
        + " items to " + filePath);
  }

  /**
   * Mock read a list from disk by swapping in a visibly different set of
   * items. This is to prove panel is redrawing from the controller instead of a cached copy.
   * @param filePath the file that would be read
   */
  @Override
  public void loadFavorites(String filePath) {
    if (filePath != null && filePath.contains("bad")) {
      throw new RuntimeException("Mock read failure for " + filePath);
    }
    this.lastFilePath = filePath;
    this.favorites = new MockFavoritesList("Weekend Picks");
    favorites.add(new MenuItem("Côte de bœuf", 31.00, Category.MAIN, null));
    favorites.add(new MenuItem("Savoureux", 7.50, Category.DESSERT, null));
    favorites.add(new MenuItem("Café au lait", 3.00, Category.BEVERAGE, null));
    System.out.println("MockController: loaded " + favorites.size()
        + " items from " + filePath);
  }

  /** @return the last path passed to save or load, or null when never called */
  public String getLastFilePath() {
    return lastFilePath;
  }

  /** Returns copy of every item in the cart in the order they are called. */
  public List<MenuItem> getCartAdds() {
    return new ArrayList<MenuItem>(cartAdds);
  }

  /** Returns copy of items sent to Favorites in the order they are called */
  public List<MenuItem> getFavoriteAdds() {
    return new ArrayList<MenuItem>(favoriteAdds);
  }

  /** Return the number of items within the seeded catalog */
  public int getMenuSize() {
    return menuItems.size();
  }

  /**
   * Hands back the live cart, not a copy, matching what the real controller
   * will do. The panel only reads from it.
   * @return the cart this fake owns
   */
  @Override
  public Order getCart() {
    return cart;
  }

  /**
   * Removes a line from the cart. An unknown name is ignored rather than
   * throwing, which is what {@code Order.remove} already does.
   * @param name the name of the item to remove
   */
  @Override
  public void removeFromCart(String name) {
    boolean removed = cart.remove(name);
    System.out.println("MockController: remove " + name + " returned " + removed);
  }

  /**
   * Sets the quantity of a line. A non-positive quantity or an unknown name throws exactly as the real
   * controller will. The panel's decrease guard is what keeps that unreachable.
   * @param name the name of the item
   * @param quantity the new quantity
   */
  @Override
  public void setCartQuantity(String name, int quantity) {
    cart.setQuantity(name, quantity);
    System.out.println("MockController: set " + name + " to " + quantity);
  }

  /**
   * Confirms the cart. Every line is checked against stock first, so a refusal
   * leaves stock and cart untouched rather than half applied. Only once every
   * line is known to be satisfiable does anything change.
   * @throws RuntimeException when a line asks for more than the stock holds
   */
  @Override
  public void checkout() {
    Map<MenuItem, Integer> lines = cart.getItemsWithQuantities();
    for (Map.Entry<MenuItem, Integer> line : lines.entrySet()) {
      String name = line.getKey().getName();
      int available = stock.containsKey(name) ? stock.get(name) : 0;
      if (line.getValue() > available) {
        throw new RuntimeException(
                "Mock refusal: only " + available + " of " + name + " left in stock");
      }
    }
    for (Map.Entry<MenuItem, Integer> line : lines.entrySet()) {
      String name = line.getKey().getName();
      stock.put(name, stock.get(name) - line.getValue());
    }
    lastCheckoutTotal = cart.getTotal();
    checkoutCount++;
    cart.clear();
    System.out.println("MockController: checkout " + checkoutCount
            + " for " + lastCheckoutTotal);
  }

  /** @return how many checkouts have completed */
  public int getCheckoutCount() {
    return checkoutCount;
  }

  /** @return the cart total at the last completed checkout */
  public double getLastCheckoutTotal() {
    return lastCheckoutTotal;
  }

  /**
   * Reads a stock level, for the demo and for tests.
   * @param itemName the item to look up
   * @return the units in stock, or zero when unknown
   */
  public int getStock(String itemName) {
    return stock.containsKey(itemName) ? stock.get(itemName) : 0;
  }
}
