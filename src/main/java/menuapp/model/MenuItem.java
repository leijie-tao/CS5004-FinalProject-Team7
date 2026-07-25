package menuapp.model;

/** One item on the menu. A single class, distinguished only by its category. */
public class MenuItem {

  private final String name;
  private final double price;
  private final Category category;
  private final String imagePath;

  /**
   * Creates a menu item.
   *
   * @param name unique display name, used as the identity of the item
   * @param price price in dollars, must not be negative
   * @param category the section this item is grouped under
   * @param imagePath path to the item image, or null when there is none
   */
  public MenuItem(String name, double price, Category category, String imagePath) {
    this.name = name;
    this.price = price;
    this.category = category;
    this.imagePath = imagePath;
  }

  /** @return the unique display name of this item */
  public String getName() {
    throw new UnsupportedOperationException("TODO");
  }

  /** @return the price in dollars */
  public double getPrice() {
    throw new UnsupportedOperationException("TODO");
  }

  /** @return the section this item is grouped under */
  public Category getCategory() {
    throw new UnsupportedOperationException("TODO");
  }

  /** @return the image path, or null when there is none */
  public String getImagePath() {
    throw new UnsupportedOperationException("TODO");
  }

  /**
   * Two items are equal when their names match.
   *
   * @param other the object to compare with
   * @return true when both are menu items with the same name
   */
  @Override
  public boolean equals(Object other) {
    throw new UnsupportedOperationException("TODO");
  }

  /** @return a hash based on the name, consistent with equals */
  @Override
  public int hashCode() {
    throw new UnsupportedOperationException("TODO");
  }
}
