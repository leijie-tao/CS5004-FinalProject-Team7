package menuapp.view;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.LinkedHashMap;
import java.util.Map;

import menuapp.model.MenuItem;
import menuapp.model.Category;


/**
 * Tests the display helpers behind cart screens. Every method is a package-private static with no Swing dependency.
 * Because of this, the class can run without opening a window.
 * Note, there is no deliberate test that will call on the controller in these tests.
 */
public class OrderPanelTest {

    /**
     * Builds menu item for item name, unit price, item category, and menu item. Image not implemented at this time so
     * no image.
     * @param name item name
     * @param price unit price
     * @param category item category
     * @return a menu item
     */
    private MenuItem item(String name, double price, Category category) {
        return new MenuItem(name, price, category, null);
    }

    private Map<MenuItem, Integer> cartFixture() {
        Map<MenuItem, Integer> cart = new LinkedHashMap<MenuItem, Integer>();
        cart.put(item("Cazuela de castañas", 29.50, Category.MAIN), 1);
        cart.put(item("Štrúdl", 6.601, Category.DESSERT), 10);
        cart.put(item("Yuenyeung", 4, Category.BEVERAGE), 4);
        return cart;
    }

    // Column Headers

    /**
     * Columns are in a fixed order with quantity & subtotal outside of the shared 3 columns.
     */
    @Test
    public void CartColumnIsItemPriceQuantitySubtotal() {
        String[] names = OrderPanel.cartColumnNames();
        assertEquals(4, names.length);
        assertEquals("Item", names[0]);
        assertEquals("Price", names[1]);
        assertEquals("Qty", names[2]);
        assertEquals("Subtotal", names[3]);
    }

    /** Each caller gets its own array so no one panel can overwrite another's headers. */
    @Test
    public void cartColumnNamesHandsBackAFreshArray() {
        assertNotSame(OrderPanel.cartColumnNames(), OrderPanel.cartColumnNames());
    }

    // Row Building

    /** One row per cart line with a total of four cells each. */
    @Test
    public void buildCartRowsProducesOneRowPerLine() {
        Object[][] rows = OrderPanel.buildCartRows(cartFixture());
        assertEquals(3, rows.length);
        assertEquals(4, rows[0].length);
    }

    /** A null cart is treated like an empty one rather than throwing. */
    @Test
    public void buildCartRowsHandlesNull() {
        assertEquals(0, OrderPanel.buildCartRows(null).length);
    }

    /** Table does not reshuffle on every redraw. */
    @Test
    public void buildCartRowsKeepsInsertionOrder() {
        Object[][] rows = OrderPanel.buildCartRows(cartFixture());
        assertEquals("Cazuela de castañas", rows[0][0]);
        assertEquals("Štrúdl", rows[1][0]);
        assertEquals("Yuenyeung", rows[2][0]);
    }

    /** Price column shows unit price only and not the line total. */
    @Test
    public void buildCartRowsShowsUnitPrice() {
        Object[][] rows = OrderPanel.buildCartRows(cartFixture());
        assertEquals("$29.50", rows[0][1]);
        assertEquals("$6.60", rows[1][1]);
        assertEquals("$4.00", rows[2][1]);
    }

    /** Quantity renders as display text like every other cell. */
    @Test
    public void buildCartRowsRendersQuantityAsText() {
        Object[][] rows = OrderPanel.buildCartRows(cartFixture());
        assertEquals("1", rows[0][2]);
        assertEquals("10", rows[1][2]);
        assertEquals("4", rows[2][2]);
    }

    /** The subtotal column multiplies unit price by quantity. */
    @Test
    public void buildCartRowsMultipliesSubtotal() {
        Object[][] rows = OrderPanel.buildCartRows(cartFixture());
        assertEquals("$29.50", rows[0][3]);
        assertEquals("$66.01", rows[1][3]);
        assertEquals("$16.00", rows[2][3]);
    }

    // Header and total lines

    /** Header text reflects the number of distinct lines. */
    @Test
    public void buildHeaderTextPluralisesOnLineCount() {
        assertEquals("Cart (1 item)", OrderPanel.buildHeaderText(1));
        assertEquals("Cart (2 items)", OrderPanel.buildHeaderText(2));
        assertEquals("Cart (0 items)", OrderPanel.buildHeaderText(0));
    }

    /** The total line is carries only two decimal places. */
    @Test
    public void buildTotalTextLabelsAndFormats() {
        assertEquals("Total: $37.00", OrderPanel.buildTotalText(37.0));
        assertEquals("Total: $0.00", OrderPanel.buildTotalText(0.0));
    }

    // Reading a quantity back out of the cart
    /** Cart reports item current quantity. */
    @Test
    public void quantityOfFindsTheLine() {
        assertEquals(1, OrderPanel.quantityOf(cartFixture(), "Cazuela de castañas"));
        assertEquals(10, OrderPanel.quantityOf(cartFixture(), "Štrúdl"));
    }

    /** A name not in the cart reads as zero rather than throwing. */
    @Test
    public void quantityOfReturnsZeroWhenAbsent() {
        assertEquals(0, OrderPanel.quantityOf(cartFixture(), "Grilled Salmon"));
    }

    /** A null cart or a null name reads as zero so that a stale click cannot crash a redraw. */
    @Test
    public void quantityOfHandlesNull() {
        assertEquals(0, OrderPanel.quantityOf(null, "Štrúdl"));
        assertEquals(0, OrderPanel.quantityOf(cartFixture(), null));
    }

    // Decreasing Number Works
    /**
     * At quantity one, decreasing would call {@code Order.setQuantity} with zero,
     * which throws. Result is that the line is removed instead.
     */
    @Test
    public void shouldRemoveOnDecreaseAtQuantityOne() {
        assertEquals(true, OrderPanel.shouldRemoveOnDecrease(1));
    }

    /** If Above one then decreasing is a plain quantity change. */
    @Test
    public void shouldNotRemoveOnDecreaseAboveOne() {
        assertEquals(false, OrderPanel.shouldRemoveOnDecrease(2));
        assertEquals(false, OrderPanel.shouldRemoveOnDecrease(9));
    }

    /**
     * Zero and negative quantities should never reach this method, but they route
     * to removal rather than to a call that is certain to throw.
     */
    @Test
    public void shouldRemoveOnDecreaseGuardsImpossibleQuantities() {
        assertEquals(true, OrderPanel.shouldRemoveOnDecrease(0));
        assertEquals(true, OrderPanel.shouldRemoveOnDecrease(-3));
    }

    // Selection survival across a redraw
    /** A selection still inside the table survives the rebuild unchanged. */
    @Test
    public void clampSelectionKeepsAValidRow() {
        assertEquals(1, OrderPanel.clampSelection(1, 3));
        assertEquals(0, OrderPanel.clampSelection(0, 3));
    }

    /**
     * Removing the last line leaves the old index past the end. Selection falls
     * back to the new last row instead of vanishing.
     */
    @Test
    public void clampSelectionFallsBackToTheLastRow() {
        assertEquals(1, OrderPanel.clampSelection(2, 2));
        assertEquals(0, OrderPanel.clampSelection(5, 1));
    }

    /** An empty table can select nothing. */
    @Test
    public void clampSelectionReturnsNothingForAnEmptyTable() {
        assertEquals(-1, OrderPanel.clampSelection(0, 0));
        assertEquals(-1, OrderPanel.clampSelection(-1, 0));
    }

    /** No prior selection stays no selection. */
    @Test
    public void clampSelectionKeepsAnAbsentSelectionAbsent() {
        assertEquals(-1, OrderPanel.clampSelection(-1, 4));
    }
}
