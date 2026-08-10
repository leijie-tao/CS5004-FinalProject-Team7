package menuapp.model;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Concrete catalog of menu items. Implements {@link Menu} and holds the item
 * collection used by the rest of the application.
 */
public class RestaurantMenu implements Menu {

  @Override
  public void addItem(MenuItem item) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public boolean removeItem(String name) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public List<MenuItem> getAllItems() {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public Map<Category, List<MenuItem>> groupByCategory() {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public List<MenuItem> sortedBy(Comparator<MenuItem> order) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public List<MenuItem> search(String keyword) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public List<MenuItem> itemsInCategory(Category category) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public int size() {
    throw new UnsupportedOperationException("TODO");
  }
}