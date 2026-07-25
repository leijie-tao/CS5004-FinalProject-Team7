package menuapp.view;

import menuapp.controller.AppController;

/**
 * Customer screen to view, build, save, load, and modify favorites. Favorites
 * do not feed the cart or checkout.
 */
public class FavoritesPanel extends AppPanel {

  /**
   * Creates the favorites screen.
   *
   * @param controller the shared controller
   */
  public FavoritesPanel(AppController controller) {
    super(controller);
  }

  @Override
  public void refresh() {
    throw new UnsupportedOperationException("TODO");
  }
}
