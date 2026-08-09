package menuapp.view;


import menuapp.controller.AppController;

import javax.swing.*;
import java.awt.*;

/**
 * Staff screen that shows revenue by category as a bar chart, plus order count
 * and total revenue.
 */
public class SalesChartPanel extends AppPanel {

  /** Placeholder notice shown until the real screen is written. */
  static final String PLACEHOLDER_TEXT = "Sales chart screen: not built yet.";

  /**
   * Creates the sales chart screen.
   * @param controller the shared controller
   */
  public SalesChartPanel(AppController controller) {
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