package menuapp.view;

import menuapp.controller.AppController;

/**
 * Staff screen for the stock table, restock controls, and exporting the low
 * stock sub-list to JSON.
 */
public class InventoryPanel extends AppPanel {

  /**
   * Creates the inventory screen.
   * @param controller the shared controller
   */
  public InventoryPanel(AppController controller) {
    super(controller);
  }

  @Override
  public void refresh() {
    throw new UnsupportedOperationException("TODO");
  }
}
