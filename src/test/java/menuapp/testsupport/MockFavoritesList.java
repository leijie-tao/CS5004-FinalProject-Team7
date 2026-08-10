package menuapp.testsupport;

import java.util.ArrayList;
import java.util.List;

import menuapp.model.FavoritesList;
import menuapp.model.MenuItem;

//  NOTE: overrides contains() with .equals because the real FavoritesList.contains uses getName().contains(name)
//  and not equality. Since add() guards on contains(), adding "Tea" to a list already holding "Green Tea" is dropped.

/**
 * A working stand-in for {@link FavoritesList} used only while the view is
 * built ahead of the model. This subclass overrides each UnsupportedOperationException from real fave list
 * methods with a plain {@code ArrayList} behind it. This class exists so {@link MockController} has something real to hand back, and it is deleted
 * once the model team finishes {@code FavoritesList}.
 */
public class MockFavoritesList extends FavoritesList {

    /**
     * The actual storage the real class will eventually own.
     */
    private final List<MenuItem> items = new ArrayList<MenuItem>();

    /**
     * The label of this list. Saved locally because the superclass stores its own
     * copy in a private field that has no working getter yet.
     */
    private String listName;

    /**
     * Creates an empty stand-in list with a label.
     *
     * @param name the label shown in the panel header
     */
    public MockFavoritesList(String name) {
        super(name);
        this.listName = name;
    }

    /**
     * Adds one item with duplicate detection uses the item name.
     *
     * @param item the item to add
     */
    @Override
    public void add(MenuItem item) {
        if (item == null) {
            return;
        }
        if (!contains(item.getName())) {
            items.add(item);
        }
    }

    /**
     * Adds every item and skips duplicates.
     *
     * @param newItems the items to add
     */
    @Override
    public void addAll(List<MenuItem> newItems) {
        if (newItems == null) {
            return;
        }
        for (MenuItem item : newItems) {
            add(item);
        }
    }

    /**
     * Removes the item with the given name.
     *
     * @param name the name of the item to remove
     * @return true when an item was actually removed
     */
    @Override
    public boolean remove(String name) {
        for (int index = 0; index < items.size(); index++) {
            if (items.get(index).getName().equals(name)) {
                items.remove(index);
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether an item with the given name is in the list.
     *
     * @param name the name of the item
     * @return true when the item is present
     */
    @Override
    public boolean contains(String name) {
        for (MenuItem item : items) {
            if (item.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a defensive copy to prevent mutating panel list by accident.
     *
     * @return a new list holding the current items
     */
    @Override
    public List<MenuItem> getItems() {
        return new ArrayList<MenuItem>(items);
    }

    /**
     * @return how many items are in the list
     */
    @Override
    public int size() {
        return items.size();
    }

    /**
     * @return the label of this list
     */
    @Override
    public String getName() {
        return listName;
    }

    /**
     * Renames the list.
     *
     * @param name the new label
     */
    @Override
    public void setName(String name) {
        this.listName = name;
    }
}