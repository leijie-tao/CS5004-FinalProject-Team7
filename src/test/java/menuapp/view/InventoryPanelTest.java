package menuapp.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests the helper methods used by the inventory screen. These tests check how inventory data is turned
 * into table rows, labels, parsed numbers, error messages, and export file names without opening the GUI.
 * Controller actions are not tested here because they are covered separately through manual GUI testing.
 */
public class InventoryPanelTest {

    /**
     * Creates sample inventory data for the tests. The items are added out of alphabetical order so the
     * tests can check that the table sorts them correctly instead of relying on the map's order.
     * @return the fixture map
     */
    private Map<String, Integer> stockFixture() {
        Map<String, Integer> stock = new HashMap<String, Integer>();
        stock.put("Yuenyeung", 20);
        stock.put("Durian Ice Cream", 2);
        stock.put("Štrúdl", 20);
        stock.put("Café au lait", 0);
        return stock;
    }

    /**
     * Creates sample low-stock item names for the tests. The names are already sorted to match
     * the order returned by {@code Inventory.lowStockItems}.
     @return the sample low-stock item names
     */
    private List<String> lowStockFixture() {
        List<String> names = new ArrayList<String>();
        names.add("Café au lait");
        names.add("Durian Ice Cream");
        return names;
    }

    // Column headers

    /** Two columns in a fixed order, shared by the stock and low stock tables. */
    @Test
    public void stockColumnsAreItemAndInStock() {
        String[] names = InventoryPanel.stockColumnNames();
        assertEquals(2, names.length);
        assertEquals("Item", names[0]);
        assertEquals("In stock", names[1]);
    }

    /** Each caller gets its own array, so one table cannot overwrite the other's headers. */
    @Test
    public void stockColumnNamesHandsBackAFreshArray() {
        String[] first = InventoryPanel.stockColumnNames();
        assertNotSame(first, InventoryPanel.stockColumnNames());
        first[0] = "Overwritten";
        assertEquals("Item", InventoryPanel.stockColumnNames()[0]);
    }

    /** The name column is the one a selected row is identified by. */
    @Test
    public void nameIsTheFirstColumn() {
        assertEquals(0, InventoryPanel.STOCK_NAME_COLUMN);
    }

    // Stock rows

    /** One row per stocked item, two cells each. */
    @Test
    public void buildStockRowsProducesOneRowPerItem() {
        Object[][] rows = InventoryPanel.buildStockRows(stockFixture());
        assertEquals(4, rows.length);
        assertEquals(2, rows[0].length);
    }

    /** A null map is treated like an empty one rather than throwing. */
    @Test
    public void buildStockRowsHandlesNull() {
        assertEquals(0, InventoryPanel.buildStockRows(null).length);
    }

    /** An empty inventory produces no rows, not one blank row. */
    @Test
    public void buildStockRowsHandlesEmpty() {
        assertEquals(0, InventoryPanel.buildStockRows(new HashMap<String, Integer>()).length);
    }

    /**
     * Checks that inventory rows are sorted by item name. This keeps the table order consistent even when the
     * inventory map does not provide a reliable iteration order. The test also confirms that the sorting matches
     * the order used for low-stock items because both tables agree on ordering.
     * For example, Štrúdl lands after Yuenyeung.
     */
    @Test
    public void buildStockRowsSortsByName() {
        Object[][] rows = InventoryPanel.buildStockRows(stockFixture());
        assertEquals("Café au lait", rows[0][0]);
        assertEquals("Durian Ice Cream", rows[1][0]);
        assertEquals("Yuenyeung", rows[2][0]);
        assertEquals("Štrúdl", rows[3][0]);
    }

    /** Counts render as display text, like every other cell in this project. */
    @Test
    public void buildStockRowsRendersCountAsText() {
        Object[][] rows = InventoryPanel.buildStockRows(stockFixture());
        assertEquals("0", rows[0][1]);
        assertEquals("2", rows[1][1]);
        assertEquals("20", rows[2][1]);
    }

    // Low stock rows

    /** One row per low stock name, in the order the controller supplied. */
    @Test
    public void buildLowStockRowsProducesOneRowPerName() {
        Object[][] rows = InventoryPanel.buildLowStockRows(lowStockFixture(), stockFixture());
        assertEquals(2, rows.length);
        assertEquals(2, rows[0].length);
    }

    /** Checks that the low-stock rows keep the same order they were given.
     * Since the low-stock items are already sorted, the helper should not reorder them.
     */
    @Test
    public void buildLowStockRowsKeepsTheSuppliedOrder() {
        Object[][] rows = InventoryPanel.buildLowStockRows(lowStockFixture(), stockFixture());
        assertEquals("Café au lait", rows[0][0]);
        assertEquals("Durian Ice Cream", rows[1][0]);
    }

    /** Each name is joined back to its count */
    @Test
    public void buildLowStockRowsJoinsNamesToCounts() {
        Object[][] rows = InventoryPanel.buildLowStockRows(lowStockFixture(), stockFixture());
        assertEquals("0", rows[0][1]);
        assertEquals("2", rows[1][1]);
    }

    /**
     * Checks that a low-stock item with no matching stock count is treated as having zero units
     * instead of causing an error.
     */
    @Test
    public void buildLowStockRowsTreatsAMissingCountAsZero() {
        List<String> names = new ArrayList<String>();
        names.add("Ghost Item");
        Object[][] rows = InventoryPanel.buildLowStockRows(names, stockFixture());
        assertEquals("Ghost Item", rows[0][0]);
        assertEquals("0", rows[0][1]);
    }

    /** No low stock names means no rows. */
    @Test
    public void buildLowStockRowsHandlesEmptyAndNullNames() {
        assertEquals(0,
                InventoryPanel.buildLowStockRows(new ArrayList<String>(), stockFixture()).length);
        assertEquals(0, InventoryPanel.buildLowStockRows(null, stockFixture()).length);
    }

    /** A null stock map still renders the names, every count reading zero. */
    @Test
    public void buildLowStockRowsHandlesNullStock() {
        Object[][] rows = InventoryPanel.buildLowStockRows(lowStockFixture(), null);
        assertEquals(2, rows.length);
        assertEquals("0", rows[0][1]);
    }

    // Header lines

    /** The header counts distinct stocked items and pluralises on that count. */
    @Test
    public void buildHeaderTextPluralisesOnItemCount() {
        assertEquals("Inventory (1 item)", InventoryPanel.buildHeaderText(1));
        assertEquals("Inventory (4 items)", InventoryPanel.buildHeaderText(4));
        assertEquals("Inventory (0 items)", InventoryPanel.buildHeaderText(0));
    }

    /**
     * Checks that the low-stock header shows the threshold used to find low-stock items, along with
     * how many items matched that threshold.
     */
    @Test
    public void buildLowStockHeaderTextNamesTheThreshold() {
        assertEquals("Low stock at or below 5 (2 items)",
                InventoryPanel.buildLowStockHeaderText(5, 2));
        assertEquals("Low stock at or below 0 (1 item)",
                InventoryPanel.buildLowStockHeaderText(0, 1));
    }

    /** An empty sub-list still states the threshold, so the screen never goes blank. */
    @Test
    public void buildLowStockHeaderTextHandlesEmpty() {
        assertEquals("Low stock at or below 3 (0 items)",
                InventoryPanel.buildLowStockHeaderText(3, 0));
    }

    // Parsing typed numbers
    /** Plain digits parse to the number they spell. */
    @Test
    public void parseIntAtLeastAcceptsDigits() {
        assertEquals(7, InventoryPanel.parseIntAtLeast("7", 1));
        assertEquals(120, InventoryPanel.parseIntAtLeast("120", 1));
    }

    /** Stray spaces around a number are the user's, not an error. */
    @Test
    public void parseIntAtLeastTrimsSurroundingSpace() {
        assertEquals(7, InventoryPanel.parseIntAtLeast("  7  ", 1));
    }

    /** Nothing typed is a failure, not a zero. */
    @Test
    public void parseIntAtLeastRejectsNullAndBlank() {
        assertEquals(InventoryPanel.INVALID_NUMBER, InventoryPanel.parseIntAtLeast(null, 1));
        assertEquals(InventoryPanel.INVALID_NUMBER, InventoryPanel.parseIntAtLeast("", 1));
        assertEquals(InventoryPanel.INVALID_NUMBER, InventoryPanel.parseIntAtLeast("   ", 1));
    }

    /** Text that is not a whole number fails rather than reaching the controller. */
    @Test
    public void parseIntAtLeastRejectsNonNumbers() {
        assertEquals(InventoryPanel.INVALID_NUMBER, InventoryPanel.parseIntAtLeast("abc", 1));
        assertEquals(InventoryPanel.INVALID_NUMBER, InventoryPanel.parseIntAtLeast("3.5", 1));
        assertEquals(InventoryPanel.INVALID_NUMBER, InventoryPanel.parseIntAtLeast("5 units", 1));
    }

    /** A number too large fails the same way as letters because caller can't tell the difference. */
    @Test
    public void parseIntAtLeastRejectsOverflow() {
        assertEquals(InventoryPanel.INVALID_NUMBER,
                InventoryPanel.parseIntAtLeast("99999999999", 1));
    }

    /**
     * Checks that the parser enforces the minimum value given by the caller.
     * Restock amounts must be at least one, while stock thresholds are allowed to be zero.
     */
    @Test
    public void parseIntAtLeastEnforcesTheCallersFloor() {
        assertEquals(InventoryPanel.INVALID_NUMBER, InventoryPanel.parseIntAtLeast("0", 1));
        assertEquals(0, InventoryPanel.parseIntAtLeast("0", 0));
        assertEquals(InventoryPanel.INVALID_NUMBER, InventoryPanel.parseIntAtLeast("-4", 0));
    }

    /**
     * Checks that the value used to represent an invalid number is below every
     * valid minimum value, so it cannot be mistaken for a real input.
     */
    @Test
    public void invalidNumberIsBelowEveryLegalFloor() {
        assertEquals(-1, InventoryPanel.INVALID_NUMBER);
    }

    /** The threshold the screen opens on must itself be a legal threshold. */
    @Test
    public void defaultThresholdIsLegal() {
        assertTrue(InventoryPanel.DEFAULT_THRESHOLD >= 0);
        assertEquals(InventoryPanel.DEFAULT_THRESHOLD,
                InventoryPanel.parseIntAtLeast(
                        String.valueOf(InventoryPanel.DEFAULT_THRESHOLD), 0));
    }

    // Explaining a rejected number

    /** The message states the rule and quotes back what was typed. */
    @Test
    public void buildInvalidNumberMessageStatesRuleAndInput() {
        assertEquals(
                "Restock amount must be a whole number of at least 1. Got: \"abc\".",
                InventoryPanel.buildInvalidNumberMessage("Restock amount", "abc", 1));
    }

    /** The floor in the message is the floor that was actually applied. */
    @Test
    public void buildInvalidNumberMessageReportsTheFloorUsed() {
        assertEquals(
                "Threshold must be a whole number of at least 0. Got: \"-4\".",
                InventoryPanel.buildInvalidNumberMessage("Threshold", "-4", 0));
    }

    /**
     * Nothing typed means there is nothing to quote back. Showing an empty pair
     * of quotes reads like a broken message, so the rule sentence stands alone.
     */
    @Test
    public void buildInvalidNumberMessageOmitsBlankInput() {
        assertEquals("Threshold must be a whole number of at least 0.",
                InventoryPanel.buildInvalidNumberMessage("Threshold", "", 0));
        assertEquals("Threshold must be a whole number of at least 0.",
                InventoryPanel.buildInvalidNumberMessage("Threshold", "   ", 0));
        assertEquals("Threshold must be a whole number of at least 0.",
                InventoryPanel.buildInvalidNumberMessage("Threshold", null, 0));
    }


    // Export filename

    /**
     * Checks that a JSON extension is added when the file name does not already have one,
     * so the exported file is clearly identified as a JSON file.
     */
    @Test
    public void ensureJsonExtensionAppendsWhenMissing() {
        assertEquals("low-stock.json", InventoryPanel.ensureJsonExtension("low-stock"));
    }

    /** A name that already ends in the extension is left alone. */
    @Test
    public void ensureJsonExtensionLeavesAnExistingOne() {
        assertEquals("low-stock.json", InventoryPanel.ensureJsonExtension("low-stock.json"));
    }

    /** The check ignores case, so a shouted extension is not doubled up. */
    @Test
    public void ensureJsonExtensionIgnoresCase() {
        assertEquals("low-stock.JSON", InventoryPanel.ensureJsonExtension("low-stock.JSON"));
    }

    /**
     * Checks that a JSON extension is added even when the file name already has a different extension.
     * The existing name is kept and ".json" is added to the end.
     */
    @Test
    public void ensureJsonExtensionAppendsAfterAnotherExtension() {
        assertEquals("report.txt.json", InventoryPanel.ensureJsonExtension("report.txt"));
    }

    /** Surrounding space is dropped before the extension is decided. */
    @Test
    public void ensureJsonExtensionTrimsFirst() {
        assertEquals("low-stock.json", InventoryPanel.ensureJsonExtension("  low-stock  "));
    }

    /**
     * Checks that null and blank file names are returned unchanged instead of adding a JSON extension
     * when there is no actual file name.
     */
    @Test
    public void ensureJsonExtensionHandlesNullAndBlank() {
        assertNull(InventoryPanel.ensureJsonExtension(null));
        assertEquals("", InventoryPanel.ensureJsonExtension(""));
        assertEquals("   ", InventoryPanel.ensureJsonExtension("   "));
    }
}