package menuapp.view;

import java.awt.BorderLayout;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import menuapp.controller.AppController;
import menuapp.model.MenuItem;

/**
 * Customer screen for cart items, quantities, total, and checkout.
 */
public class OrderPanel extends AppPanel {

  /** Placeholder notice shown until the real screen is written. */
  static final String PLACEHOLDER_TEXT = "Cart screen: not built yet.";

  /**
   * Creates the cart screen.
   * @param controller the shared controller
   */
  public OrderPanel(AppController controller) {
    super(controller);
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

  /** Column headers for the cart table. Category is dropped and quantity and
   * subtotal are added. */
  private static final String[] CART_COLUMN_NAMES = {"Item", "Price", "Qty", "Subtotal"};

  /** Index of the item name column, the column that identifies a selected row. */
  static final int NAME_COLUMN = 0;

  /**
   * Returns the cart column headers as a fresh copy on every call.
   * @return a new array holding the four headers
   */
  static String[] cartColumnNames() {
    return CART_COLUMN_NAMES.clone();
  }

  /**
   * Converts cart lines into the row data the table displays. A linked hash map remembers the order things were added
   * so if an item does get added (i.e., burger, fries, sprite --> table would show: Burger, Fries, Sprite) to prevent
   * shuffling after every redraw.
   * The subtotal multiplies the unrounded price and formats the product instead of multiplying. An item priced at
   * 6.601 therefore shows a unit price of $6.60 and a subtotal of $66.01 at quantity ten.
   * @param cart each item paired with its quantity and may be null
   * @return one row per line holding name, unit price, quantity, and subtotal;
   */
  static Object[][] buildCartRows(Map<MenuItem, Integer> cart) {
    if (cart == null) {
      return new Object[0][CART_COLUMN_NAMES.length];
    }
    Object[][] rows = new Object[cart.size()][CART_COLUMN_NAMES.length];
    int rowIndex = 0;
    for (Map.Entry<MenuItem, Integer> line : cart.entrySet()) {
      MenuItem item = line.getKey();
      int quantity = line.getValue();
      rows[rowIndex][0] = item.getName();
      rows[rowIndex][1] = ItemTableFormat.formatPrice(item.getPrice());
      rows[rowIndex][2] = String.valueOf(quantity);
      rows[rowIndex][3] = ItemTableFormat.formatPrice(item.getPrice() * quantity);
      rowIndex++;
    }
    return rows;
  }

  /**
   * Builds the header line above the table. It counts distinct lines rather than
   * total units, so one item is read instead of three (if there are three present).
   * @param lineCount how many distinct items the cart holds
   * @return the header text
   */
  static String buildHeaderText(int lineCount) {
    String unit = (lineCount == 1) ? "item" : "items";
    return "Cart (" + lineCount + " " + unit + ")";
  }

  /**
   * Builds the running total line beneath the table.
   * @param total the cart total in dollars
   * @return the total text, for example {@code Total: $37.00}
   */
  static String buildTotalText(double total) {
    return "Total: " + ItemTableFormat.formatPrice(total);
  }

  /**
   * Looks through the cart for an item with a matching name and return its quantity.
   * If the cart, name or item doesn't exist,then it returns a 0 instead of causing an error.
   * @param cart each item paired with its quantity, may be null
   * @param name the item name to look for, may be null
   * @return the quantity on that line, or zero when it is not in the cart
   */
  static int quantityOf(Map<MenuItem, Integer> cart, String name) {
    if (cart == null || name == null) {
      return 0;
    }
    for (Map.Entry<MenuItem, Integer> line : cart.entrySet()) {
      if (line.getKey().getName().equals(name)) {
        return line.getValue();
      }
    }
    return 0;
  }

  /**
   * If the quantity is 1 or less than 1, returns true. Otherwise, remove the item from the cart instead of reducing quantity.
   * There should be no negative or 0, and if it does occur, item is removed;.
   * @param currentQuantity the quantity showing on the line
   * @return true when the line should be removed
   */
  static boolean shouldRemoveOnDecrease(int currentQuantity) {
    return currentQuantity <= 1;
  }

  /**
   * Figures which row should stay selected after the table refreshes by trying to keep the same row highlighted. If
   * the row no longer exists, then the last available row is selected instead. If there are no rows or nothing was
   * selected before , then it selects nothing.
   * @param previousRow the row index selected before the rebuild. Set to -1 when nothing was selected
   * @param rowCount how many rows the table holds after the rebuild
   * @return the row index to select, or -1 to select nothing
   */
  static int clampSelection(int previousRow, int rowCount) {
    if (rowCount <= 0 || previousRow < 0) {
      return -1;
    }
    if (previousRow >= rowCount) {
      return rowCount - 1;
    }
    return previousRow;
  }
}