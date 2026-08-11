package menuapp;

import javax.swing.SwingUtilities;

import menuapp.controller.AppController;
import menuapp.model.Category;
import menuapp.model.Inventory;
import menuapp.model.MenuItem;
import menuapp.model.RestaurantMenu;
import menuapp.persistence.FileHandler;
import menuapp.persistence.JsonFileHandler;
import menuapp.view.MainFrame;

/**
 * Entry point. Builds the model, the single AppController, and the GUI, then
 * shows the window. It injects the shared Menu, Inventory, and FileHandler into
 * the controller.
 */
public class MainApp {

  /**
   * Starts the application.
   *
   * @param args unused
   */
  public static void main(String[] args) {
    RestaurantMenu menu = new RestaurantMenu();
    menu.addItem(new MenuItem("Burger", 8.99, Category.MAIN, "/images/burger.png"));
    menu.addItem(new MenuItem("Sandwich", 6.50, Category.MAIN, "/images/sandwich.png"));
    menu.addItem(new MenuItem("Salad", 7.00, Category.MAIN, "/images/salad.png"));
    menu.addItem(new MenuItem("Cake", 5.50, Category.DESSERT, "/images/cake.png"));
    menu.addItem(new MenuItem("Ice Cream", 3.00, Category.DESSERT, "/images/ice_cream.png"));
    menu.addItem(new MenuItem("Coffee", 4.50, Category.BEVERAGE, "/images/coffee.png"));
    menu.addItem(new MenuItem("Tea", 5.00, Category.BEVERAGE, "/images/tea.png"));

    Inventory inventory = new Inventory();
    inventory.setStock("Burger", 25);
    inventory.setStock("Sandwich", 20);
    inventory.setStock("Salad", 5);
    inventory.setStock("Cake", 5);
    inventory.setStock("Ice Cream", 20);
    inventory.setStock("Coffee", 30);
    inventory.setStock("Tea", 25);

    FileHandler files = new JsonFileHandler();
    AppController controller = new AppController(menu, inventory, files);

    SwingUtilities.invokeLater(() -> {
      MainFrame frame = new MainFrame(controller);
      frame.setVisible(true);
    });
  }
}
