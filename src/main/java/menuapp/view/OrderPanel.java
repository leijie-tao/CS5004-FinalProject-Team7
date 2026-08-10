package menuapp.view;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import menuapp.controller.AppController;

/**
 * Customer screen for cart items, quantities, total, and checkout.
 */
public class OrderPanel extends AppPanel {

  /** Placeholder notice shown until the real screen is written. */
  static final String PLACEHOLDER_TEXT = "Cart screen: not built yet.";

  /**
   * Creates the cart screen.
   * @param controller the shared controller
   */
  public OrderPanel(AppController controller) {
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