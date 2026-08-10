package menuapp.model;

import java.util.List;

/**
 * A named list of items a customer views, edits, saves, and loads. It is not
 * part of the ordering path; checkout always goes through the cart. Saving and
 * loading this list backs the optional load and modify feature.
 */
public class FavoritesList {

  private String name;

  /**
   * Creates an empty favorites list with a label.
   *
   * @param name the label shown for this list, also the file base name
   */
  public FavoritesList(String name) {
    this.name = name;
  }

  /**
   * Adds one item to the list.
   *
   * @param item the item to add
   */
  public void add(MenuItem item) {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * Adds every item in a batch, for example a whole category.
   *
   * @param items the items to add
   */
  public void addAll(List<MenuItem> items) {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * Removes an item from the list.
   *
   * @param name the name of the item to remove
   * @return true when the item was present
   */
  public boolean remove(String name) {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * Tests whether an item is in the list.
   *
   * @param name the name of the item
   * @return true when the item is present
   */
  public boolean contains(String name) {
    throw new UnsupportedOperationException("TODO");
  }

  /** @return the items in the list */
  public List<MenuItem> getItems() {
    throw new UnsupportedOperationException("TODO");
  }

  /** @return the number of items */
  public int size() {
    throw new UnsupportedOperationException("TODO");
  }

  /** @return the label of this list */
  public String getName() {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * Renames the list.
   *
   * @param name the new label
   */
  public void setName(String name) {
    throw new UnsupportedOperationException("TODO");
  }
}