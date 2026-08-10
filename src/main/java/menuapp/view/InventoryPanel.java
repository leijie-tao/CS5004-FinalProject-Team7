package menuapp.view;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import menuapp.controller.AppController;

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
}