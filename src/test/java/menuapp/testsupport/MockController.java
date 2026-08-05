package menuapp.testsupport;

import menuapp.controller.AppController;
import menuapp.model.Category;
import menuapp.model.FavoritesList;
import menuapp.model.MenuItem;

/**
 * A stand-in {@link AppController} that lets the favorites screen be built and
 * run before the real controller exists.
 * At integration time nothing in {@code FavoritesPanel} changes. You simply
 * pass the real {@code AppController} into the constructor instead of this one.
 */
public class MockController extends AppController {

  /** The list this fake hands back, pre-loaded so the table is never empty. */
  private MockFavoritesList favorites;

  /** The last path passed to save or load, handy for eyeballing behavior. */
  private String lastFilePath;

  /** Creates a fake controller holding a small seeded favorites list. */
  public MockController() {
    super(null, null, null);
    this.favorites = new MockFavoritesList("My Favorites");
    seedStartingItems();
  }

  /** Fills the list with realistic sample items.*/
  private void seedStartingItems() {
    favorites.add(new MenuItem("Margherita Pizza", 14.50, Category.MAIN, null));
    favorites.add(new MenuItem("Grilled Salmon", 22.00, Category.MAIN, null));
    favorites.add(new MenuItem("Tiramisu", 8.75, Category.DESSERT, null));
    favorites.add(new MenuItem("Iced Latte", 4.25, Category.BEVERAGE, null));
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
    favorites.add(new MenuItem("Ribeye Steak", 31.00, Category.MAIN, null));
    favorites.add(new MenuItem("Lemon Tart", 7.50, Category.DESSERT, null));
    favorites.add(new MenuItem("Espresso", 3.00, Category.BEVERAGE, null));
    System.out.println("FakeController: loaded " + favorites.size()
        + " items from " + filePath);
  }

  /** @return the last path passed to save or load, or null when never called */
  public String getLastFilePath() {
    return lastFilePath;
  }
}
