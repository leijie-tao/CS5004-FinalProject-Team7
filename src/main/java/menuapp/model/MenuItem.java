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
    return name;
  }

  /** @return the price in dollars */
  public double getPrice() {
    return price;
  }

  /** @return the section this item is grouped under */
  public Category getCategory() {
    return category;
  }

  /** @return the image path, or null when there is none */
  public String getImagePath() {
    return imagePath;
  }

  /**
   * Two items are equal when their names match.
   *
   * @param other the object to compare with
   * @return true when both are menu items with the same name
   */
  @Override
  public boolean equals(Object other) {
    if(this == other) {
      return true;
    }
    if(!(other instanceof MenuItem)) {
      return false;
    }
    return this.name.equals(((MenuItem)other).name);
  }

  /** @return a hash based on the name, consistent with equals */
  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
