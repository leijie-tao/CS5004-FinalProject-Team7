package menuapp.view;

import menuapp.controller.AppController;

/**
 * Staff screen that shows revenue by category as a bar chart, plus order count
 * and total revenue. Display only; nothing is exported from this panel.
 */
public class SalesChartPanel extends AppPanel {

  /**
   * Creates the sales chart screen.
   *
   * @param controller the shared controller
   */
  public SalesChartPanel(AppController controller) {
    super(controller);
  }

  @Override
  public void refresh() {
    throw new UnsupportedOperationException("TODO");
  }
}
