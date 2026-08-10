package menuapp.model;

import java.util.*;

/**
 * Concrete catalog of menu items. Implements {@link Menu} and holds the item
 * collection used by the rest of the application.
 */
public class RestaurantMenu implements Menu {

  /** Build a new list to store MenuItem items. */
  private final List<MenuItem> items = new ArrayList<>();


  /**
   * Adds an item to the catalog.
   *
   * @param item the item to add
   */
  @Override
  public void addItem(MenuItem item) {
    items.add(item);
  }

  /**
   * Removes the item with the given name.
   *
   * @param name the unique name of the item to remove
   * @return true when an item was removed
   */
  @Override
  public boolean removeItem(String name) {
    return items.removeIf(item -> item.getName().equals(name));
  }

  /** @return a new list of every item in insertion order */
  @Override
  public List<MenuItem> getAllItems() {
    return new ArrayList<>(items);
  }

  /**
   * Groups every item by its category for the sectioned view.
   * This backs the required organized view feature.
   *
   * @return a new map from category to a new list of items in that category
   */
  @Override
  public Map<Category, List<MenuItem>> groupByCategory() {
    Map<Category, List<MenuItem>> group = new EnumMap<>(Category.class);
    for(MenuItem item : items) {
      Category cat = item.getCategory();
      List<MenuItem> list = group.get(cat);
      if(list == null) {
        list = new ArrayList<>();
        group.put(cat, list);
      }
      list.add(item);
    }
    return group;
  }

  /**
   * Returns every item in a chosen order. A small bonus, not one of the five.
   *
   * @param order the comparator that defines the order
   * @return a new sorted list
   */
  @Override
  public List<MenuItem> sortedBy(Comparator<MenuItem> order) {
    List<MenuItem> sorted = new ArrayList<>(items);
    sorted.sort(order);
    return sorted;
  }

  /**
   * Finds items whose name contains the keyword. Backs the search feature.
   *
   * @param keyword the text to look for
   * @return a new list of the matching items
   */
  @Override
  public List<MenuItem> search(String keyword) {
    List<MenuItem> result = new ArrayList<>();
    for(MenuItem item : items) {
      if(item.getName().toLowerCase().contains(keyword.toLowerCase())) {
        result.add(item);
      }
    }
    return result;
  }

  /**
   * Returns the items in one category, used to build a favorites sub-list.
   *
   * @param category the category to collect
   * @return a new list of the items in that category
   */
  @Override
  public List<MenuItem> itemsInCategory(Category category) {
    List<MenuItem> result = new ArrayList<>();
    for(MenuItem item : items) {
      if(item.getCategory() == category) {
        result.add(item);
      }
    }
    return result;
  }

  /** @return the number of items in the catalog */
  @Override
  public int size() {
    return items.size();
  }
}