package menuapp.view;

import menuapp.controller.AppController;

/**
 * Customer screen to browse by category, filter by category, search, and add
 * items to the cart or favorites.
 */
public class MenuPanel extends AppPanel {

  /**
   * Creates the menu screen.
   *
   * @param controller the shared controller
   */
  public MenuPanel(AppController controller) {
    super(controller);
  }

  @Override
  public void refresh() {
    throw new UnsupportedOperationException("TODO");
  }
}
