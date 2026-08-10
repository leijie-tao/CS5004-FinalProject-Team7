package menuapp.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumMap;
import java.util.Map;

import menuapp.model.Category;
import org.junit.jupiter.api.Test;

/**
 * Tests the display helpers behind the sales chart. Every method under test is
 * a package-private static that reads a revenue map and returns a number or a
 * string, so this class runs headless without drawing a chart.
 */
public class SalesChartPanelTest {

    /**
     * Revenue with two categories earning and one still at zero.
     * @return the fixture map
     */
    private Map<Category, Double> revenueFixture() {
        Map<Category, Double> revenue = new EnumMap<Category, Double>(Category.class);
        revenue.put(Category.MAIN, 62.50);
        revenue.put(Category.DESSERT, 6.60);
        revenue.put(Category.BEVERAGE, 0.0);
        return revenue;
    }

    // Reading one category only
    /** A category that has earned reports what it earned. */
    @Test
    public void revenueForFindsTheCategory() {
        assertEquals(62.50, SalesChartPanel.revenueFor(revenueFixture(), Category.MAIN));
        assertEquals(6.60, SalesChartPanel.revenueFor(revenueFixture(), Category.DESSERT));
    }

    /**
     * A category missing from the map reads as zero. The chart draws a bar for
     * every category, so an absent entry must still produce a number.
     */
    @Test
    public void revenueForTreatsAMissingCategoryAsZero() {
        Map<Category, Double> revenue = new EnumMap<Category, Double>(Category.class);
        revenue.put(Category.MAIN, 10.0);
        assertEquals(0.0, SalesChartPanel.revenueFor(revenue, Category.BEVERAGE));
    }

    /** A null map or a null category reads as zero rather than throwing. */
    @Test
    public void revenueForHandlesNull() {
        assertEquals(0.0, SalesChartPanel.revenueFor(null, Category.MAIN));
        assertEquals(0.0, SalesChartPanel.revenueFor(revenueFixture(), null));
    }

    // Totalling revenue
    /** The total is the sum of every category. */
    @Test
    public void totalOfSumsEveryCategory() {
        assertEquals(69.10, SalesChartPanel.totalOf(revenueFixture()), 0.001);
    }

    /** Nothing to total is zero, so this is not an error. */
    @Test
    public void totalOfHandlesNullAndEmpty() {
        assertEquals(0.0, SalesChartPanel.totalOf(null));
        assertEquals(0.0, SalesChartPanel.totalOf(new EnumMap<Category, Double>(Category.class)));
    }

    // Deciding whether there is anything to draw

    /** Any category above zero means there is a chart worth showing. */
    @Test
    public void hasRevenueIsTrueWhenSomethingEarned() {
        assertTrue(SalesChartPanel.hasRevenue(revenueFixture()));
    }

    /**
     * Every category at zero means no sale has been recorded yet. The chart
     * would draw three flat bars, which tells the user nothing, so the empty
     * state is shown instead.
     */
    @Test
    public void hasRevenueIsFalseWhenEverythingIsZero() {
        Map<Category, Double> revenue = new EnumMap<Category, Double>(Category.class);
        revenue.put(Category.MAIN, 0.0);
        revenue.put(Category.DESSERT, 0.0);
        revenue.put(Category.BEVERAGE, 0.0);
        assertFalse(SalesChartPanel.hasRevenue(revenue));
    }

    /** An empty or missing map is the same as nothing earned. */
    @Test
    public void hasRevenueHandlesNullAndEmpty() {
        assertFalse(SalesChartPanel.hasRevenue(null));
        assertFalse(SalesChartPanel.hasRevenue(new EnumMap<Category, Double>(Category.class)));
    }

    // Header line
    /** The header carries the running total, formatted as currency. */
    @Test
    public void buildHeaderTextShowsTheTotal() {
        assertEquals("Revenue by category (total $69.10)",
                SalesChartPanel.buildHeaderText(69.10));
        assertEquals("Revenue by category (total $0.00)",
                SalesChartPanel.buildHeaderText(0.0));
    }
}