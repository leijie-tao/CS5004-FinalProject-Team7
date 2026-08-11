package menuapp.view;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import menuapp.model.Category;
import menuapp.model.MenuItem;


/**
 * Tests display of FavoritesPanel and contains method that turns model objects into text.
 * These tests live in package so they can reach the private methods without making them public or going
 * through getter method or calling a method that calls the private method.
 */
public class FavoritesPanelTest {

  /**
   * Small list of items created to render.
   * @return two items in different categories
   */
  private List<MenuItem> sampleItems() {
    List<MenuItem> items = new ArrayList<MenuItem>();
    items.add(new MenuItem("Margherita Pizza", 14.5, Category.MAIN, null));
    items.add(new MenuItem("Tiramisu", 8.0, Category.DESSERT, null));
    return items;
  }

  /** An empty list should produce no rows rather than blowing up. */
  @Test
  public void buildRowsReturnsNoRows() {
    assertEquals(0, FavoritesPanel.buildRows(new ArrayList<MenuItem>()).length);
  }

  /** A null list is treated like an empty one. */
  @Test
  public void buildRowsHandleNull() { assertEquals(0, FavoritesPanel.buildRows(null).length);
  }

  /** One row per item, with three columns each. */
  @Test
  public void buildRowsProducesOneRowPerItem() {
    Object[][] rows = FavoritesPanel.buildRows(sampleItems());
    assertEquals(2, rows.length);
    assertEquals(3, rows[0].length);
  }

  /** Names come through unchanged. */
  @Test
  public void buildRowsPutsNameInFirstColumn() {
    Object[][] rows = FavoritesPanel.buildRows(sampleItems());
    assertEquals("Margherita Pizza", rows[0][0]);
  }

  /** Prices show with two decimal places. */
  @Test
  public void buildRowsFormatsPriceAsCurrency() {
    Object[][] rows = FavoritesPanel.buildRows(sampleItems());
    assertEquals("$14.50", rows[0][2]);
    assertEquals("$8.00", rows[1][2]);
  }

  /** Enum constants are readable cases, not shouted. */
  @Test
  public void buildRowsFormatsCategoryReadably() {
    Object[][] rows = FavoritesPanel.buildRows(sampleItems());
    assertEquals("Main", rows[0][1]);
    assertEquals("Dessert", rows[1][1]);
  }

  /** A missing category renders as blank rather than the text "null". */
  @Test
  public void formatCategoryHandlesNull() {
    assertEquals("", FavoritesPanel.formatCategory(null));
  }

  /** The header pluralises correctly. */
  @Test
  public void buildHeaderTextUsesSingularForOneItem() {
    assertEquals("My List (1 item)", FavoritesPanel.buildHeaderText("My List", 1));
  }

  /** Zero and many both read as "items". */
  @Test
  public void buildHeaderTextUsesPluralOtherwise() {
    assertEquals("My List (0 items)", FavoritesPanel.buildHeaderText("My List", 0));
    assertEquals("My List (4 items)", FavoritesPanel.buildHeaderText("My List", 4));
  }

  /** A missing list name falls back to a sensible default. */
  @Test
  public void buildHeaderTextFallsBackWhenNameIsNull() {
    assertEquals("Favorites (2 items)", FavoritesPanel.buildHeaderText(null, 2));
  }
}