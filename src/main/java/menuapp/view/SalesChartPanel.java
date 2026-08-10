package menuapp.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import menuapp.controller.AppController;
import menuapp.model.Category;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Displays the revenue earned for each category as a bar chart. The chart is updated whenever the screen
 * is refreshed using the latest revenue data from the controller.
 */
public class SalesChartPanel extends AppPanel {
    /** Shows the running total above the chart.*/
    private final JLabel headerLabel;
    /** Shown in place of the chart before any sale has been recorded. */
    private final JLabel emptyStateLabel;
    /** The numbers the chart draws. Updated in place, never rebuilt. */
    private final DefaultCategoryDataset dataset;
    /** The chart's container. This is the swappable center region. */
    private final ChartPanel chartPanel;

    /**
     * Creates the sales chart screen.
     * @param controller the shared controller
     */
    public SalesChartPanel(AppController controller) {
        super(controller, "Sales");

        this.headerLabel = new JLabel();
        this.emptyStateLabel = new JLabel(
                "No sales yet. Check out an order to see revenue here!", SwingConstants.CENTER);
        this.dataset = new DefaultCategoryDataset();

        JFreeChart chart = ChartFactory.createBarChart(
                CHART_TITLE, CATEGORY_AXIS_LABEL, VALUE_AXIS_LABEL, dataset);
        this.chartPanel = new ChartPanel(chart);

        // Dependency chain. Do not reorder!
        layOutComponents();
        refresh();
    }

    /**
     * Puts the header at the top and the chart in the center slot, which is the
     * precondition {@link AppPanel#showEmptyState} relies on to swap it out.
     */
    private void layOutComponents() {
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        add(headerLabel, BorderLayout.NORTH);
        add(chartPanel);
    }

    /**
     * Updates the chart with the latest revenue data from the controller with every category as a given a value,
     * even if it has no recorded revenue. This is so the chart always shows the same set of categories.
     * The existing dataset is updated each time instead of creating a new one.
     */
    @Override
    public void refresh() {
        Map<Category, Double> revenue;
        try {
            revenue = controller.getRevenueByCategory();
        } catch (UnsupportedOperationException notBuiltYet) {
            showNotReady("getRevenueByCategory");
            return;
        }

        for (Category category : Category.values()) {
            dataset.setValue(revenueFor(revenue, category),
                    REVENUE_SERIES, ItemTableFormat.formatCategory(category));
        }
        headerLabel.setText(buildHeaderText(totalOf(revenue)));
        showEmptyState(!hasRevenue(revenue), chartPanel, emptyStateLabel);
    }

    // Statics--decisions about values but with no chart

    /** Title drawn above the bars by JFreeChart. */
    private static final String CHART_TITLE = "Revenue by category";
    /** Label under the horizontal axis. */
    private static final String CATEGORY_AXIS_LABEL = "Category";
    /** Label beside the vertical axis. */
    private static final String VALUE_AXIS_LABEL = "Revenue ($)";
    /** The single series every bar belongs to. One series means one bar per category. */
    private static final String REVENUE_SERIES = "Revenue";

    /**
     * Reads one category's revenue out of the map.
     * @param revenue  revenue accumulated per category, may be null
     * @param category the category to look up, may be null
     * @return the revenue for that category, or zero when it is absent
     */
    static double revenueFor(Map<Category, Double> revenue, Category category) {
        if (revenue == null || category == null) {
            return 0.0;
        }
        Double amount = revenue.get(category);
        if (amount == null) {
            return 0.0;
        }
        return amount;
    }

    /**
     * Adds up revenue across every category.
     * @param revenue revenue accumulated per category, may be null
     * @return the total, or zero when there is nothing to add
     */
    static double totalOf(Map<Category, Double> revenue) {
        if (revenue == null) {
            return 0.0;
        }
        double total = 0.0;
        for (Category category : Category.values()) {
            total += revenueFor(revenue, category);
        }
        return total;
    }

    /**
     * Reports whether anything has been earned yet. A chart of three flat bars
     * tells the reader nothing, so the empty state is shown instead.
     * @param revenue revenue accumulated per category, may be null
     * @return true when at least one category is above zero
     */
    static boolean hasRevenue(Map<Category, Double> revenue) {
        return totalOf(revenue) > 0.0;
    }

    /**
     * Builds the header line above the chart.
     * @param totalRevenue revenue across every category
     * @return the header text, for example {@code Revenue by category (total $69.10)}
     */
    static String buildHeaderText(double totalRevenue) {
        return CHART_TITLE + " (total " + ItemTableFormat.formatPrice(totalRevenue) + ")";
    }
}