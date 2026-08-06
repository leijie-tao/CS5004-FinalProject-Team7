package menuapp.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import menuapp.model.Category;
import menuapp.model.MenuItem;
import org.junit.jupiter.api.Test;

/** Tests MenuPanel's methods for what the table shows and what the status line reads. */
public class MenuPanelTest {
    /**
     * Builds a menu item for use in the fixtures below.
     * @param name the item name
     * @param price the item price
     * @param category the item category
     * @return a new menu item with no image
     */
    private MenuItem item(String name, double price, Category category) {
        return new MenuItem(name, price, category, null);
    }

    /**
     * A grouped menu holding two mains, one dessert, and no beverages. The
     * beverage key is absent rather than mapped to an empty list, which is what a
     * real grouping is likely to hand back.
     * @return the grouped fixture
     */
    private Map<Category, List<MenuItem>> groupedFixture() {
        Map<Category, List<MenuItem>> grouped =
                new LinkedHashMap<Category, List<MenuItem>>();
        List<MenuItem> mains = new ArrayList<MenuItem>();
        mains.add(item("Margherita Pizza", 14.5, Category.MAIN));
        mains.add(item("Grilled Salmon", 22.0, Category.MAIN));
        List<MenuItem> desserts = new ArrayList<MenuItem>();
        desserts.add(item("Tiramisu", 8.0, Category.DESSERT));
        grouped.put(Category.DESSERT, desserts);
        grouped.put(Category.MAIN, mains);
        return grouped;
    }

    // Flatted group
    /** Every item in the grouped map ends up in the flat list. */
    @Test
    public void flattenGroupedKeepsEveryItem() {
        assertEquals(3, MenuPanel.flattenGrouped(groupedFixture()).size());
    }

    /**
     * Sections come out in enum declaration order, not in the map's own key
     * order. The fixture deliberately puts desserts in the map first.
     */
    @Test
    public void flattenGroupedOrdersByCategoryDeclaration() {
        List<MenuItem> flattened = MenuPanel.flattenGrouped(groupedFixture());
        assertEquals("Margherita Pizza", flattened.get(0).getName());
        assertEquals("Grilled Salmon", flattened.get(1).getName());
        assertEquals("Tiramisu", flattened.get(2).getName());
    }

    /** A category with no entry in the map is skipped rather than throwing. */
    @Test
    public void flattenGroupedSkipsMissingCategory() {
        List<MenuItem> flattened = MenuPanel.flattenGrouped(groupedFixture());
        for (MenuItem menuItem : flattened) {
            assertFalse(menuItem.getCategory() == Category.BEVERAGE);
        }
    }

    /** A null map is treated as an empty menu. */
    @Test
    public void flattenGroupedHandlesNull() {
        assertEquals(0, MenuPanel.flattenGrouped(null).size());
    }

    /** An empty map produces an empty list. */
    @Test
    public void flattenGroupedHandlesEmptyMap() {
        assertEquals(0,
                MenuPanel.flattenGrouped(
                        new LinkedHashMap<Category, List<MenuItem>>()).size());
    }

    //Narrowed category
    /**
     * A small mixed list used to check narrowing.
     *
     * @return two mains and one dessert
     */
    private List<MenuItem> mixedItems() {
        List<MenuItem> items = new ArrayList<MenuItem>();
        items.add(item("Margherita Pizza", 14.5, Category.MAIN));
        items.add(item("Tiramisu", 8.0, Category.DESSERT));
        items.add(item("Grilled Salmon", 22.0, Category.MAIN));
        return items;
    }

    /** Narrowing keeps only the chosen category. */
    @Test
    public void narrowToCategoryKeepsOnlyThatCategory() {
        List<MenuItem> narrowed =
                MenuPanel.narrowToCategory(mixedItems(), Category.MAIN);
        assertEquals(2, narrowed.size());
        assertEquals("Margherita Pizza", narrowed.get(0).getName());
        assertEquals("Grilled Salmon", narrowed.get(1).getName());
    }

    /** A null category means no filter, so everything survives. */
    @Test
    public void narrowToCategoryKeepsEverythingWhenCategoryIsNull() {
        assertEquals(3, MenuPanel.narrowToCategory(mixedItems(), null).size());
    }

    /** A category nothing matches yields an empty list rather than null. */
    @Test
    public void narrowToCategoryCanReturnNothing() {
        assertEquals(0,
                MenuPanel.narrowToCategory(mixedItems(), Category.BEVERAGE).size());
    }

    /** A null list is treated as an empty one. */
    @Test
    public void narrowToCategoryHandlesNullList() {
        assertEquals(0, MenuPanel.narrowToCategory(null, Category.MAIN).size());
    }

    /** Narrowing never edits the list it was handed. */
    @Test
    public void narrowToCategoryLeavesTheOriginalAlone() {
        List<MenuItem> original = mixedItems();
        MenuPanel.narrowToCategory(original, Category.MAIN);
        assertEquals(3, original.size());
    }

    // Category Labels
    /** The combo offers the all categories entry plus one label per category. */
    @Test
    public void buildCategoryLabelsStartsWithAllCategories() {
        String[] labels = MenuPanel.buildCategoryLabels();
        assertEquals(Category.values().length + 1, labels.length);
        assertEquals(MenuPanel.ALL_CATEGORIES_LABEL, labels[0]);
    }

    /** Labels are readable rather than shouted enum names. */
    @Test
    public void buildCategoryLabelsAreReadable() {
        String[] labels = MenuPanel.buildCategoryLabels();
        assertEquals("Main", labels[1]);
        assertEquals("Dessert", labels[2]);
        assertEquals("Beverage", labels[3]);
    }

    /** Every generated label maps back to the category it came from. */
    @Test
    public void categoryFromLabelRoundTripsEveryCategory() {
        for (Category category : Category.values()) {
            String label = ItemTableFormat.formatCategory(category);
            assertEquals(category, MenuPanel.categoryFromLabel(label));
        }
    }

    /** The all categories entry means no filter. */
    @Test
    public void categoryFromLabelReturnsNullForAllCategories() {
        assertNull(MenuPanel.categoryFromLabel(MenuPanel.ALL_CATEGORIES_LABEL));
    }

    /** An unknown or missing label is treated as no filter rather than crashing. */
    @Test
    public void categoryFromLabelHandlesUnknownAndNull() {
        assertNull(MenuPanel.categoryFromLabel("Appetiser"));
        assertNull(MenuPanel.categoryFromLabel(null));
    }

    // Searching items

    /** Real text counts as a search. */
    @Test
    public void isSearchingTrueForRealText() {
        assertTrue(MenuPanel.isSearching("pizza"));
    }

    /** Null, empty, and whitespace-only text all count as no search. */
    @Test
    public void isSearchingFalseForBlankText() {
        assertFalse(MenuPanel.isSearching(null));
        assertFalse(MenuPanel.isSearching(""));
        assertFalse(MenuPanel.isSearching("   "));
    }

    //Build Status Texts

    /** With no keyword and no filter the status is a plain item count. */
    @Test
    public void buildStatusTextCountsItemsWhenBrowsing() {
        assertEquals("8 items", MenuPanel.buildStatusText(8, "", null));
    }

    /** One item reads in the singular. */
    @Test
    public void buildStatusTextUsesSingularForOneItem() {
        assertEquals("1 item", MenuPanel.buildStatusText(1, null, null));
    }

    /** A filter alone names the category. */
    @Test
    public void buildStatusTextNamesTheCategoryWhenFiltering() {
        assertEquals("3 items in Dessert",
                MenuPanel.buildStatusText(3, "", Category.DESSERT));
    }

    /** A keyword switches the noun to matches and echoes the keyword. */
    @Test
    public void buildStatusTextReportsMatchesWhenSearching() {
        assertEquals("2 matches for \"pizza\"",
                MenuPanel.buildStatusText(2, "pizza", null));
        assertEquals("1 match for \"pizza\"",
                MenuPanel.buildStatusText(1, "pizza", null));
    }

    /** A keyword and a filter together describe both. */
    @Test
    public void buildStatusTextDescribesSearchAndFilterTogether() {
        assertEquals("1 match for \"lemon\" in Beverage",
                MenuPanel.buildStatusText(1, "lemon", Category.BEVERAGE));
    }

    /** Surrounding spaces are trimmed out of the echoed keyword. */
    @Test
    public void buildStatusTextTrimsTheKeyword() {
        assertEquals("0 matches for \"tart\"",
                MenuPanel.buildStatusText(0, "  tart  ", null));
    }
}