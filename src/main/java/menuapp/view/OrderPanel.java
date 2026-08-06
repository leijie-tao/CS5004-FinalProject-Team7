package menuapp.view;

import menuapp.controller.AppController;

/** Customer screen for cart items, quantities, total, and checkout. */
public class OrderPanel extends AppPanel {

  /**
   * Creates the order (cart) screen.
   * @param controller the shared controller
   */
  public OrderPanel(AppController controller) {
    super(controller);
  }

  @Override
  public void refresh() {
    throw new UnsupportedOperationException("TODO");
  }
}
