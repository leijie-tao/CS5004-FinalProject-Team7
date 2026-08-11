package menuapp.view;

import java.util.ArrayList;
import java.util.List;
import menuapp.model.Category;
import menuapp.model.MenuItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the shared formatting that both {@code FavoritesPanel} and {@code MenuPanel} render through.
 * {@code FavoritesPanelTest} already covers the same behavior through its own panel;
 * these cases pin it at the source so a change made for one panel cannot quietly break the other.
 */
public class ItemTableFormatTest {

    /**
     * Two items in different categories.
     * @return the fixture list
     */
    private List<MenuItem> sampleItems() {
        List<MenuItem> items = new ArrayList<MenuItem>();
        items.add(new MenuItem("Margherita Pizza", 14.5, Category.MAIN, null));
        items.add(new MenuItem("Tiramisu", 8.0, Category.DESSERT, null));
        return items;
    }

    /** Three columns in a fixed order. */
    @Test
    public void columnNamesAreItemCategoryPrice() {
        String[] names = ItemTableFormat.columnNames();
        assertEquals(3, names.length);
        assertEquals("Item", names[0]);
        assertEquals("Category", names[1]);
        assertEquals("Price", names[2]);
    }

    /** Each caller gets its own array. Overwriting a header in one panel doesn't change the headers in another. */
    @Test
    public void columnNamesHandsBackAFreshArray() {
        String[] first = ItemTableFormat.columnNames();
        String[] second = ItemTableFormat.columnNames();
        assertNotSame(first, second);
        first[0] = "Overwritten";
        assertEquals("Item", ItemTableFormat.columnNames()[0]);
    }

    /** One row per item, three cells each. */
    @Test
    public void buildRowsProducesOneRowPerItem() {
        Object[][] rows = ItemTableFormat.buildRows(sampleItems());
        assertEquals(2, rows.length);
        assertEquals(3, rows[0].length);
    }

    /** A null list is treated like an empty one. */
    @Test
    public void buildRowsHandlesNull() {
        assertEquals(0, ItemTableFormat.buildRows(null).length);
    }

    /** Names pass through unchanged. */
    @Test
    public void buildRowsPutsNameInFirstColumn() {
        assertEquals("Margherita Pizza", ItemTableFormat.buildRows(sampleItems())[0][0]);
    }

    /** Categories render in readable case. */
    @Test
    public void buildRowsFormatsCategoryReadably() {
        Object[][] rows = ItemTableFormat.buildRows(sampleItems());
        assertEquals("Main", rows[0][1]);
        assertEquals("Dessert", rows[1][1]);
    }

    /** Prices carry two decimal places and a dollar sign. */
    @Test
    public void buildRowsFormatsPriceAsCurrency() {
        Object[][] rows = ItemTableFormat.buildRows(sampleItems());
        assertEquals("$14.50", rows[0][2]);
        assertEquals("$8.00", rows[1][2]);
    }

    /** A missing category renders blank rather than the text "null". */
    @Test
    public void formatCategoryHandlesNull() {
        assertEquals("", ItemTableFormat.formatCategory(null));
    }

    /** Whole and fractional prices both get two decimals. */
    @Test
    public void formatPriceAlwaysShowsTwoDecimals() {
        assertEquals("$3.00", ItemTableFormat.formatPrice(3.0));
        assertEquals("$0.05", ItemTableFormat.formatPrice(0.05));
        assertEquals("$31.00", ItemTableFormat.formatPrice(31.0));
    }

    /** Any shouted enum name comes back readable, for roles as well as categories. */
    @Test
    public void formatEnumNameReadsReadably() {
        assertEquals("Beverage", ItemTableFormat.formatEnumName("BEVERAGE"));
        assertEquals("Customer", ItemTableFormat.formatEnumName("CUSTOMER"));
    }

    /** Nothing to format renders blank rather than the text "null". */
    @Test
    public void formatEnumNameHandlesNullAndEmpty() {
        assertEquals("", ItemTableFormat.formatEnumName(null));
        assertEquals("", ItemTableFormat.formatEnumName(""));
    }

    /** A selection still inside the table survives the rebuild unchanged. */
    @Test
    public void clampSelectionKeepsAValidRow() {
        assertEquals(1, ItemTableFormat.clampSelection(1, 3));
        assertEquals(0, ItemTableFormat.clampSelection(0, 3));
    }

    /**
     * Removing the last line leaves the old index past the end. Selection falls back to the
     * new last row instead of vanishing.
     */
    @Test
    public void clampSelectionFallsBackToTheLastRow() {
        assertEquals(1, ItemTableFormat.clampSelection(2, 2));
        assertEquals(0, ItemTableFormat.clampSelection(5, 1));
    }

    /** An empty table can select nothing. */
    @Test
    public void clampSelectionReturnsNothingForAnEmptyTable() {
        assertEquals(-1, ItemTableFormat.clampSelection(0, 0));
        assertEquals(-1, ItemTableFormat.clampSelection(-1, 0));
    }

    /** No prior selection stays no selection. */
    @Test
    public void clampSelectionKeepsAnAbsentSelectionAbsent() {
        assertEquals(-1, ItemTableFormat.clampSelection(-1, 4));
    }

    // Image preview
    /** No path means no picture. */
    @Test
    public void loadPreviewHandlesNull() {
        assertNull(ItemTableFormat.loadPreview(null));
    }

    /** Whitespace is treated the same as no path at all. */
    @Test
    public void loadPreviewHandlesBlank() {
        assertNull(ItemTableFormat.loadPreview("   "));
    }

    /** A path that resolves to nothing returns null rather than throwing. */
    @Test
    public void loadPreviewHandlesMissingResource() {
        assertNull(ItemTableFormat.loadPreview("/images/does-not-exist.png"));
    }
}