package menuapp.view;

import java.util.List;
import java.util.Locale;
import menuapp.model.Category;
import menuapp.model.MenuItem;

/**
 * Shared display formatting for every panel that shows menu item objects in a table. {@code FavoritesPanel}
 * and {@code MenuPanel} render three columns with food name, food type, and price.
 * Conversion from model object to display text lives here only once instead copying each individually per panel.
 */
final class ItemTableFormat {
    /** Column headers shared by every item table. */
    private static final String[] COLUMN_NAMES = {"Item", "Category", "Price"};
    /** Index of the item name column, the column that identifies a selected row. */
    static final int NAME_COLUMN = 0;
    /** Prevents instantiation; this class is a holder for static helpers only. */
    private ItemTableFormat() {
        throw new AssertionError("ItemTableFormat is not meant to be instantiated");
    }

    /**
     * Returns the column headers for an item table as a fresh copy on every call.
     * @return a new array holding the three column headers, in display order
     */
    static String[] columnNames() {
        return COLUMN_NAMES.clone();
    }

    /**
     * Converts menu items into the row data a {@code DefaultTableModel} displays.
     * @param items the items to display, may be null
     * @return one row per item holding name, readable category, formatted price and null if empty
     */
    static Object[][] buildRows(List<MenuItem> items) {
        if (items == null) {
            return new Object[0][COLUMN_NAMES.length];
        }
        Object[][] rows = new Object[items.size()][COLUMN_NAMES.length];
        for (int rowIndex = 0; rowIndex < items.size(); rowIndex++) {
            MenuItem item = items.get(rowIndex);
            rows[rowIndex][0] = item.getName();
            rows[rowIndex][1] = formatCategory(item.getCategory());
            rows[rowIndex][2] = formatPrice(item.getPrice());
        }
        return rows;
    }

    /**
     * Turns an enum constant into readable text, so {@code BEVERAGE} reads as {@code Beverage}.
     * @param category the category to format, may be null
     * @return the display text for that category, or an empty string when null
     */
    static String formatCategory(Category category) {
        if (category == null) {
            return "";
        }
        return formatEnumName(category.name());
    }

    /**
     * Turns any enum constant name into readable display text, so {@code BEVERAGE} reads as {@code Beverage}
     * and {@code CUSTOMER} reads as {@code Customer}.
     * @param rawName the enum constant name, may be null or empty
     * @return the display text, or an empty string when there is nothing to
     * format
     */
    static String formatEnumName(String rawName) {
        if (rawName == null || rawName.isEmpty()) {
            return "";
        }
        return rawName.charAt(0) + rawName.substring(1).toLowerCase(Locale.US);
    }

    /**
     * Formats a price for display with two decimal places.
     * {@link Locale#US} is passed so the separator is a dot on every machine.
     * Without it the same code prints {@code $14,50} under a European default locale.
     * (TODO: Note to me, come back here later--maybe I can do an enum for setting lcoale?)
     * @param price the price in dollars
     * @return the price as text, for example {@code $14.50}
     */
    static String formatPrice(double price) {
        return String.format(Locale.US, "$%.2f", price);
    }

    /**
     * Decides which row should stay selected after the table is rebuilt. It keeps the previous row selected
     * when possible, uses the new last row if that row no longer exists, and selects nothing when the table
     * is empty or no row was selected before.
     * @param previousRow the row selected before the rebuild, or -1 when nothing was selected
     * @param rowCount how many rows the table contains after the rebuild
     * @return the row to select, or -1 when nothing should be selected
     */
    static int clampSelection(int previousRow, int rowCount) {
        if (rowCount <= 0 || previousRow < 0) {
            return -1;
        }
        if (previousRow >= rowCount) {
            return rowCount - 1;
        }
        return previousRow;
    }


}