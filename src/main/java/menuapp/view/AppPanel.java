package menuapp.view;

import javax.swing.JPanel;
import menuapp.controller.AppController;

/** Shared base for every screen. Holds the controller and the redraw contract. */
public abstract class AppPanel extends JPanel {
  /** The controller every panel talks to. */
  protected final AppController controller;


  /**
   * Stores the controller for the subclass to use.
   * @param controller the shared controller
   */
  protected AppPanel(AppController controller) {
    this.controller = controller;
  }

  /** Redraws this panel from the current model state. */
  public abstract void refresh();
}
