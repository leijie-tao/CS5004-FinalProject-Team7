package menuapp.view;
import menuapp.controller.AppController;

import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;


/** Shared base for every screen. Holds the controller and the redraw contract.
 *  Note: AppPanel was decided to be just a class rather than interface like {@link RoleSelectionListener} since every screen comes from JPanel, an no screen
 *  has its inheritance slot spent and later get blocked. The subclass inherits the controller field which an interface
 *  couldn't do.
 * Note: Extract interface if a caller outside this menuapp package needs to hand a screen to {@code TabbedRolePanel}.
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
  /** Shown when controller method that a screen needs still throws.
   * Sole purpose is for ease of indentifying during integration.
   */
  private JLabel notReadyLabel;

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

  /**
   * Switches between normal view and empty state view. If the requested view is already showing, then nothing changes.
   * After switching the panel is then told to repaint itself so that the correct view appears on screen.
   * @param isEmpty   true when empty state should be shown
   * @param fullView  normal view to show when content is available
   * @param emptyView view to show when there is no content
   */
  protected final void showEmptyState(boolean isEmpty, Component fullView, Component emptyView) {
    if (isEmpty == showingEmptyState) {
      return;
    }
    if (isEmpty) {
      remove(fullView);
      add(emptyView, BorderLayout.CENTER);
    } else {
      remove(emptyView);
      add(fullView, BorderLayout.CENTER);
    }
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

  /**
   * Shows a message when a controller method is not available yet. The message names the missing method and then it
   * replaces the normal view with the not-ready state until the feature is implemented. Purpose is to assist with
   * integration/wiring. Since this method is not called explicitly by any panel, a try/block behavior implemented in
   * OrderPanel,MenuPanel, and FavoritePanel's {@code refresh()} for reachability
   * @param fullView normal view to hide while the feature is unavailable.
   * @param methodName controller method not implemented
   */
  protected final void showNotReady(Component fullView, String methodName) {
    if(notReadyLabel == null) {
      notReadyLabel = new JLabel("", SwingConstants.CENTER);
    }
    notReadyLabel.setText(
            "Not available yet: AppController." + methodName + " is not implemented");
    showEmptyState(true, fullView, notReadyLabel);
  }

}
