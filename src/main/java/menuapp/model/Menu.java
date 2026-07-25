package menuapp.model;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * The catalog of menu items. This is the seam the other layers depend on.
 * Every method that returns a list or map returns a fresh copy. The caller may
 * sort or edit it freely without touching the catalog.
 */
public interface Menu {

  /**
   * Adds an item to the catalog.
   *
   * @param item the item to add
   */
  void addItem(MenuItem item);

  /**
   * Removes the item with the given name.
   *
   * @param name the unique name of the item to remove
   * @return true when an item was removed
   */
  boolean removeItem(String name);

  /** @return a new list of every item in insertion order */
  List<MenuItem> getAllItems();

  /**
   * Groups every item by its category for the sectioned view.
   * This backs the required organized view feature.
   *
   * @return a new map from category to a new list of items in that category
   */
  Map<Category, List<MenuItem>> groupByCategory();

  /**
   * Returns every item in a chosen order. A small bonus, not one of the five.
   *
   * @param order the comparator that defines the order
   * @return a new sorted list
   */
  List<MenuItem> sortedBy(Comparator<MenuItem> order);

  /**
   * Finds items whose name contains the keyword. Backs the search feature.
   *
   * @param keyword the text to look for
   * @return a new list of the matching items
   */
  List<MenuItem> search(String keyword);

  /**
   * Returns the items in one category, used to build a favorites sub-list.
   *
   * @param category the category to collect
   * @return a new list of the items in that category
   */
  List<MenuItem> itemsInCategory(Category category);

  /** @return the number of items in the catalog */
  int size();
}
