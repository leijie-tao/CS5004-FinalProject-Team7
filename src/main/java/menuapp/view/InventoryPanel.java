package menuapp.view;
import menuapp.controller.AppController;

import java.awt.BorderLayout;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Staff screen for the stock table, restock controls, and exporting the low
 * stock sub-list to JSON.
 */
public class InventoryPanel extends AppPanel {

  /** Placeholder notice shown until the real screen is written. */
  static final String PLACEHOLDER_TEXT = "Inventory screen: not built yet.";

  /**
   * Creates the inventory screen.
   * @param controller the shared controller
   */
  public InventoryPanel(AppController controller) {
    super(controller); // TODO: switch to the two-arg constructor once this screen raises dialogs!
    setLayout(new BorderLayout());
    add(new JLabel(PLACEHOLDER_TEXT, SwingConstants.CENTER), BorderLayout.CENTER);
  }

  /**
   * Does nothing yet. The real version redraws this screen from the
   * controller. It must not throw, because {@code MainFrame} refreshes every
   * card it shows.
   */
  @Override
  public void refresh() {
    // TODO: redraw from the controller once this screen is built.
  }

  // Statics: display text and decisions about values, with no widget in sight

  /** Column headers shared by the stock table and the low stock sub-list table. */
  private static final String[] STOCK_COLUMN_NAMES = {"Item", "In stock"};

  /** Index of the item name column, the column that identifies a selected row. */
  static final int STOCK_NAME_COLUMN = 0;

  /** Special value returned by {@link #parseIntAtLeast} when the input cannot be used as a valid number. */
  static final int INVALID_NUMBER = -1;

  /** The default stock amount used to decide when an item is considered low in stock. */
  static final int DEFAULT_THRESHOLD = 5;

  /** Extension the export is guaranteed to carry, since the file must be JSON. */
  private static final String JSON_EXTENSION = ".json";

  /**
   * Returns the column headers for a stock table as a fresh copy on every call.
   * @return a new array holding the two headers, in display order
   */
  static String[] stockColumnNames() {
    return STOCK_COLUMN_NAMES.clone();
  }

  /**
   * Creates the rows used to display the inventory in a table. The item names are sorted before being
   * added to the table.
   * @param stock the item names and their current stock amounts
   * @return the inventory data as rows for the table
   */
  static Object[][] buildStockRows(Map<String, Integer> stock) {
    if (stock == null) {
      return new Object[0][STOCK_COLUMN_NAMES.length];
    }
    List<String> names = new ArrayList<String>(stock.keySet());
    Collections.sort(names);

    Object[][] rows = new Object[names.size()][STOCK_COLUMN_NAMES.length];
    for (int rowIndex = 0; rowIndex < names.size(); rowIndex++) {
      String name = names.get(rowIndex);
      rows[rowIndex][0] = name;
      rows[rowIndex][1] = String.valueOf(stockOf(stock, name));
    }
    return rows;
  }

  /**
   * Creates table rows for items that are low in stock. If an item's stock amount is missing, it is shown as zero.
   * @param lowStockNames the names of the low-stock items
   * @param stock the items and their current stock amounts
   * @return the low-stock items as rows for the table
   */
  static Object[][] buildLowStockRows(List<String> lowStockNames, Map<String, Integer> stock) {
    if (lowStockNames == null) {
      return new Object[0][STOCK_COLUMN_NAMES.length];
    }
    Object[][] rows = new Object[lowStockNames.size()][STOCK_COLUMN_NAMES.length];
    for (int rowIndex = 0; rowIndex < lowStockNames.size(); rowIndex++) {
      String name = lowStockNames.get(rowIndex);
      rows[rowIndex][0] = name;
      rows[rowIndex][1] = String.valueOf(stockOf(stock, name));
    }
    return rows;
  }

  /**
   * Reads one count out of a stock snapshot.
   * @param stock the snapshot, may be null
   * @param name  the item to look up
   * @return the units in stock, or zero when the snapshot or the entry is absent
   */
  private static int stockOf(Map<String, Integer> stock, String name) {
    if (stock == null || !stock.containsKey(name)) {
      return 0;
    }
    return stock.get(name);
  }

  /**
   * Builds the header line above the stock table.
   * @param itemCount how many items carry a stock entry
   * @return the header text for the low stock list
   */
  static String buildHeaderText(int itemCount) {
    return "Inventory (" + itemCount + " " + itemWord(itemCount) + ")";
  }

  /**
   * Creates the text shown above the low-stock list.
   * @param threshold the stock limit used to find low-stock items
   * @param itemCount the number of low-stock items
   * @return the header text for the low-stock list
   */
  static String buildLowStockHeaderText(int threshold, int itemCount) {
    return "Low stock at or below " + threshold
            + " (" + itemCount + " " + itemWord(itemCount) + ")";
  }

  /**
   * Picks the singular or plural noun for a count.
   * @param count the number being described
   * @return {@code item} at exactly one, {@code items} otherwise
   */
  private static String itemWord(int count) {
    return (count == 1) ? "item" : "items";
  }

  /**
   * Converts the user's text into a whole number. The number must be equal to or greater than the minimum value.
   * @param text the text entered by the user
   * @param minimum the smallest number allowed
   * @return the number, or {@link #INVALID_NUMBER} if the input is not valid
   */
  static int parseIntAtLeast(String text, int minimum) {
    if (text == null) {
      return INVALID_NUMBER;
    }
    String trimmed = text.trim();
    if (trimmed.isEmpty()) {
      return INVALID_NUMBER;
    }
    int value;
    try {
      value = Integer.parseInt(trimmed);
    } catch (NumberFormatException notANumber) {
      return INVALID_NUMBER;
    }
    if (value < minimum) {
      return INVALID_NUMBER;
    }
    return value;
  }

  /**
   * Creates an error message when the user enters an invalid number.
   * @param fieldLabel the name of the input field
   * @param rawText the text entered by the user
   * @param minimum the smallest number allowed
   * @return the error message to show the user
   */
  static String buildInvalidNumberMessage(String fieldLabel, String rawText, int minimum) {
    String rule = fieldLabel + " must be a whole number of at least " + minimum + ".";
    if (rawText == null || rawText.trim().isEmpty()) {
      return rule;
    }
    return rule + " Got: \"" + rawText.trim() + "\".";
  }

  /**
   * Makes sure the file name ends with .json. If the path is empty or missing, it is returned without changes.
   * @param path the file path entered by the user
   * @return the file path with .json added when needed
   */
  static String ensureJsonExtension(String path) {
    if (path == null) {
      return null;
    }
    String trimmed = path.trim();
    if (trimmed.isEmpty()) {
      return path;
    }
    if (trimmed.toLowerCase(Locale.US).endsWith(JSON_EXTENSION)) {
      return trimmed;
    }
    return trimmed + JSON_EXTENSION;
  }
}