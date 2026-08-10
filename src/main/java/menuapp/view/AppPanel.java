package menuapp.view;

import menuapp.controller.AppController;

import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;

/**
 * Shared base for every screen. Holds the controller and the redraw contract.
 * Note: AppPanel is a class rather than an interface like {@link RoleSelectionListener}, because every screen
 * already comes from JPanel, so no screen has its single inheritance slot spent and later gets blocked. The
 * subclass also inherits the controller field, which an interface could not give it.
 * Note: extract an interface if a caller outside this package needs to hand a screen to {@code TabbedRolePanel}.
 */
public abstract class AppPanel extends JPanel {
    /**
     * The controller every panel talks to.
     */
    protected final AppController controller;
    /**
     * Title text shown on this screen's dialogs.
     */
    private final String screenTitle;
    /**
     * The component currently occupying the center slot, or null before anything is installed.
     */
    private Component installedCenter;
    /**
     * Shown when a controller method a screen needs still throws. Sole purpose is ease of
     * identifying unfinished wiring during integration.
     */
    private JLabel notReadyLabel;

    /**
     * Stores the controller for a screen that never raises an error dialog.
     *
     * @param controller shared controller
     */
    protected AppPanel(AppController controller) {
        this(controller, "Restaurant Menu");
    }

    /**
     * Stores the controller and the title this screen's dialogs carry.
     *
     * @param controller  shared controller
     * @param screenTitle title shown on this screen's dialogs
     */
    protected AppPanel(AppController controller, String screenTitle) {
        this.controller = controller;
        this.screenTitle = screenTitle;
    }

    /**
     * Redraws this panel from the current model state.
     */
    public abstract void refresh();

    /**
     * Installs the component this panel starts out showing. Every subclass calls this from its own
     * layout method instead of adding to {@code BorderLayout.CENTER} directly, so the base class knows
     * what it is later being asked to swap out.
     *
     * @param view the component the panel starts out showing
     */
    protected final void setCenter(Component view) {
        showInCenter(view);
    }

    /**
     * Chooses between the normal view and the empty state view. Both are handed in so that neither the
     * caller nor this class has to remember which one is currently installed.
     *
     * @param isEmpty   true when the empty state should be shown
     * @param fullView  normal view to show when content is available
     * @param emptyView view to show when there is no content
     */
    protected final void showEmptyState(boolean isEmpty, Component fullView, Component emptyView) {
        showInCenter(isEmpty ? emptyView : fullView);
    }

    /**
     * Installs one component in the center slot and removing previous state. Reference identity is the
     * test, so asking for the component already showing costs nothing and a redraw does not flicker.
     * This is the only method that touches the center slot, which is why the panel can never end up
     * holding two components there at once.
     *
     * @param wanted the component that should occupy the center slot
     */
    private void showInCenter(Component wanted) {
        if (wanted == installedCenter) {
            return;
        }
        if (installedCenter != null) {
            remove(installedCenter);
        }
        add(wanted, BorderLayout.CENTER);
        installedCenter = wanted;
        revalidate();
        repaint();
    }

    /**
     * Reports a failed controller call to the user. Panels call this when a controller method throws at
     * run time, for example when a save or load fails.
     *
     * @param summary description of what failed
     * @param failure the exception that caused the failure
     */
    protected final void showFailure(String summary, RuntimeException failure) {
        JOptionPane.showMessageDialog(this, summary + ".\n" + failure.getMessage(), screenTitle, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a message when a controller method is not available yet, naming the missing method and
     * replacing the normal view until the feature is implemented. This exists to assist with wiring, and
     * is reached from the try block each panel's {@code refresh()} wraps around its controller calls.
     *
     * @param methodName the controller method that is not implemented
     */
    protected final void showNotReady(String methodName) {
        if (notReadyLabel == null) {
            notReadyLabel = new JLabel("", SwingConstants.CENTER);
        }
        notReadyLabel.setText("Not available yet: AppController." + methodName + " is not implemented");
        showInCenter(notReadyLabel);
    }
}