package menuapp.view;

import menuapp.controller.AppController;

/** First screen where the user picks customer or staff. */
public class RoleSelectionPanel extends AppPanel {

  /**
   * Creates the role selection screen.
   * @param controller the shared controller
   */
  public RoleSelectionPanel(AppController controller) {
    super(controller);
  }

  @Override
  public void refresh() {
    ;
  }
}