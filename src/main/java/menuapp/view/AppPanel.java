package menuapp.view;

import javax.swing.*;

import menuapp.controller.AppController;

import java.awt.*;

/** Shared base for every screen. Holds the controller and the redraw contract.
 *  Note: AppPanel was decided to be just a class rather than interface like {@link RoleSelectionListener} since every screen comes from JPanel, an no screen
 *  has its inheritance slot spent and later get blocked. The subclass inherits the controller field which an interface
 *  couldn't do.
 * @TODO: Extract interface if a caller outside this menuapp package needs to hand a screen to {@code TabbedRolePanel}.
 */
public abstract class AppPanel extends JPanel {
  /** The controller every panel talks to. */
  protected final AppController controller;
  /** Title text located in the error dialog screen */
  private final String screenTitle;
  /** Shows which center component is currently installed with false meaning full view is currently occupying. This is
   * also the state that everysubclass lays out unto.
   */
  private boolean showingEmptyState;
  /** Stores controller for a screen that never raises an error dialog.
   * @param controller shared controller
   */
  protected AppPanel(AppController controller) {
    this(controller, "Restaurant Menu");
  }

  /**
   * Stores controller and the title screen as well as any error flags raised.
   * @param controller shared controller
   * @param screenTitle title shown on screen
   */
protected AppPanel(AppController controller, String screenTitle) {
  this.controller = controller;
  this.screenTitle = screenTitle;
}

  /** Redraws this panel from the current model state. */
  public abstract void refresh();

  protected final void showEmptyState(boolean isEmpty, Component fullView, Component emptyView) {
    if (isEmpty == showingEmptyState) {
      return;
    }
    remove(isEmpty ? fullView : emptyView);
    add(isEmpty ? emptyView : fullView, BorderLayout.CENTER);
    showingEmptyState = isEmpty;
    revalidate();
    repaint();
  }

  /**
   * Reports failed controller call to the user, with the controller in every persistence failure at run time.
   * @param summary description of what failed
   * @param failure exception that caused the failure
   */
  protected final void showFailure(String summary, RuntimeException failure) {
    JOptionPane.showMessageDialog(
            this, summary + ".\n" + failure.getMessage(), screenTitle, JOptionPane.ERROR_MESSAGE
    );
  }

}
