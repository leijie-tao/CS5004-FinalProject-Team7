package menuapp.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import menuapp.controller.AppController;
import menuapp.model.Category;
import menuapp.model.MenuItem;

/**
 * Customer screen to browse by category, filter by category, search, and add
 * items to the cart or favorites.
 */
public class MenuPanel extends AppPanel {
  /** Column headers for the menu table that hands back fresh array in order to keep its own copy */
  private static final String[] COLUMN_NAMES = ItemTableFormat.columnNames();
  /** No category filter is applied */
  static final String ALL_CATEGORIES_LABEL = "All categories";
  /** Choose which category to show */
  private final JComboBox<String> categoryCombo;
  /** Holds search keywords also "enter" runs here the same as search button */
  private final JTextField searchField;
  /** Looks for an entered key word */
  private final JButton searchButton;
  /** Clears keyword and category filter. */
  private final JButton showAllButton;
  /** Read-only model that rebuilds with every refresh */
  private final DefaultTableModel tableModel;
  /** Shows the menu items */
  private final JTable menuTable;
  /** To use for scrolling while mainting a centered table view */
  private final JScrollPane tableScrollPane;
  /** Reports how many items are showing also echoes last added cart or favorites item. */
  private final JLabel statusLabel;
  /** Adds an item to the cart. */
  private final JButton addToCartButton;
  /** Adds an item to faves */
  private final JButton addToFavoritesButton;
  /** Tracks component layout is centered and starts false so that table is centered on run. */
  private boolean showingEmptyState;
  /** Message panel if there are no items matching the filters */
  private final JLabel emptyStateLabel;
  /** This renders the exact list that the actual table renders with index-aligned with rows. However, rows carries only
   * display test and that the selected menu item is fetched by position rather than rebuilt from table cells. This can't
   * be modified in place and is not an owned state. Items are replaced in {@link #refresh()}.
   */
  private List<MenuItem> displayedItems;


  /**
   * Creates the menu screen.
   * @param controller the shared controller
   */
  public MenuPanel(AppController controller) {
    super(controller);

    this.categoryCombo = new JComboBox<String>(buildCategoryLabels());
    this.searchField = new JTextField(16);
    this.searchButton = new JButton("Search");
    this.showAllButton = new JButton("Show all");
    this.tableModel = new ReadOnlyTableModel(COLUMN_NAMES);
    this.menuTable = new JTable(tableModel);
    this.tableScrollPane = new JScrollPane(menuTable);
    this.emptyStateLabel = new JLabel ("No match, please try another world.", SwingConstants.CENTER);
    this.statusLabel = new JLabel();
    this.addToCartButton = new JButton("\uD83D\uDED2 Add to cart"); // TODO: research if this shows up fine for macs?
    this.addToFavoritesButton = new JButton("\u2665 Add to favorites"); // TODO: research if this shows up fine for macs?
    this.displayedItems = new ArrayList<MenuItem>();

    // Dependency chain methods that read instance fields directly
    // Do not reorder!
    layOutComponents();
    attachListeners();
    refresh();
  }

  /** This makes sure that the control strip, table, and action row are arranged inside the border layout */
  private void layOutComponents() {
    setLayout(new BorderLayout(0, 8));
    setBorder(BorderFactory.createEmptyBorder(12,12,12,12)); // square

    JLabel titleLabel = new JLabel("\u2615 Our Menu ");
    titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));

    JPanel controlStrip = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    controlStrip.add(titleLabel);
    controlStrip.add(new JLabel("Category:"));
    controlStrip.add(categoryCombo);
    controlStrip.add(new JLabel("Search: "));
    controlStrip.add(searchField);
    controlStrip.add(searchButton);
    controlStrip.add(showAllButton);
    add(controlStrip, BorderLayout.NORTH);

    menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    menuTable.setRowHeight(24);
    menuTable.getTableHeader().setReorderingAllowed(false);
    add(tableScrollPane, BorderLayout.CENTER);

    JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    actionRow.add(addToCartButton);
    actionRow.add(addToFavoritesButton);
    actionRow.add(statusLabel);
    add(actionRow, BorderLayout.SOUTH);
  }

  /** Wires control to a controller call then drawing of layout */
  private void attachListeners() {
    menuTable.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
              @Override
              public void valueChanged(ListSelectionEvent event) {
                updateButtonState();
              }
            }
            );

    categoryCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        refresh();
      }
    }
    );

    searchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        refresh();
      }
    }
    );

    // Pressing Enter inside the field fires the same path as the button.
    searchField.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        refresh();
      }
    }
    );

    showAllButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        handleShowAll();
      }
    }
    );

    addToCartButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        handleAddToCart();
      }
    }
    );

    addToFavoritesButton.addActionListener(
            new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        handleAddToFavorites();
      }
    }
    );
  }


  /**
   * Redraws the whole screen from the current filter control and current model. Keywords and category
   * are read out to widgets instead of fields in order to keep only one copy of the filter state so that controls
   * and table doesn't disagree. Can be called after any change anytime.
   */
  @Override
  public void refresh() {
    String keyword = searchField.getText();
    Category category = categoryFromLabel(selectedCategoryLabel());

    List<MenuItem> items = resolveItems(keyword, category);
    displayedItems = items;

    tableModel.setDataVector(ItemTableFormat.buildRows(items), COLUMN_NAMES);
    statusLabel.setText(buildStatusText(items.size(), keyword, category));

    showEmptyState(items.isEmpty());
    updateButtonState();
  }


  // Browse by section and Default View that is laid out by section
  /**
   * Flattens the grouped menu into one list for the default sectioned view.
   * Iterates {@link Category#values()} rather than the map's own entries so the
   * sections always appear in enum declaration order. Otherwise, a  {@code HashMap} would
   * give an order that could change between runs. A category missing
   * from the map simply contributes nothing.
   * @param grouped the menu grouped by category, may be null
   * @return a new list of every item, ordered by category then by the order each category's list holds;
   * empty when {@code grouped} is null
   */
  static List<MenuItem> flattenGrouped(Map<Category, List<MenuItem>> grouped) {
    List<MenuItem> flattened = new ArrayList<MenuItem>();
    if (grouped == null) {
      return flattened;
    }
    for (Category category : Category.values()) {
      List<MenuItem> itemsInCategory = grouped.get(category);
      if (itemsInCategory != null) {
        flattened.addAll(itemsInCategory);
      }
    }
    return flattened;
  }

  // Filter by Category and Category Dropdown
  /**
   * Builds the entries for the category dropdown.
   * Labels come from {@link ItemTableFormat#formatCategory(Category)} so the
   * dropdown text matches the text in the table's category column exactly allowing
   * {@link #categoryFromLabel(String)} to map back.
   * @return a new array with every category label
   */
  static String[] buildCategoryLabels() {
    Category[] categories = Category.values();
    String[] labels = new String[categories.length + 1];
    labels[0] = ALL_CATEGORIES_LABEL;
    for (int index = 0; index < categories.length; index++) {
      labels[index + 1] = ItemTableFormat.formatCategory(categories[index]);
    }
    return labels;
  }

  /**
   * Maps a dropdown label back to the category it stands for. Compares against
   * formatted labels instead of calling {@code Category.valueOf}, which would
   * couple this method to the exact formatting rule and would throw on any unfamiliar
   * string.
   * @param label the label shown in the dropdown and can be null
   * @return the matching category
   */
  static Category categoryFromLabel(String label) {
    if (label == null || ALL_CATEGORIES_LABEL.equals(label)) {
      return null;
    }
    for (Category category : Category.values()) {
      if (ItemTableFormat.formatCategory(category).equals(label)) {
        return category;
      }
    }
    return null;
  }

  /**
   * Reads the current dropdown selection as text. Never handed null from this path.
   * @return the selected label, or {@link #ALL_CATEGORIES_LABEL} when nothing is selected
   */
  private String selectedCategoryLabel() {
    Object selected = categoryCombo.getSelectedItem();
    return (selected == null) ? ALL_CATEGORIES_LABEL : String.valueOf(selected);
  }

  // Search
  /**
   * Tests keyword as an active search. Whitespace does not count and doesn't narrow the menu to nothing.
   * @param keyword the raw text from the search field and can be null
   * @return true when the keyword holds at least one non-space character
   */
  static boolean isSearching(String keyword) {
    return keyword != null && !keyword.trim().isEmpty();
  }

  // Add to Cart
  /**
   * Adds the selected item to the cart, then redraws. Does nothing when no row
   * is selected. On failure the dialog is shown and the method returns without
   * redrawing or reporting success, so the screen never claims an add that did
   * not happen. The status message is set after {@link #refresh()} because
   * refresh rewrites the same label.
   */
  private void handleAddToCart() {
    MenuItem selectedItem = getSelectedItem();
    if (selectedItem == null) {
      return;
    }
    try {
      controller.addToCart(selectedItem);
    } catch (RuntimeException failure) {
      showFailure("Could not add that item to the cart", failure);
      return;
    }
    refresh();
    statusLabel.setText("Added " + selectedItem.getName() + " to the cart");
  }

  /**
   * Narrows a list of items down to one category.
   * {@code controller.search} looks across the whole menu and ignores the
   * category.
   * @param items the items to narrow and can be null
   * @param category the category to keep or to keep everything set to null
   * @return new list with matching items
   */
  static List<MenuItem> narrowToCategory(List<MenuItem> items, Category category) {
    List<MenuItem> narrowed = new ArrayList<MenuItem>();
    if (items == null) {
      return narrowed;
    }
    if (category == null) {
      narrowed.addAll(items);
      return narrowed;
    }
    for (MenuItem item : items) {
      if (item.getCategory() == category) {
        narrowed.add(item);
      }
    }
    return narrowed;
  }

  // Adding to Favorites
  /** Adds the selected item to favorites, then redraws. */
  private void handleAddToFavorites() {
    MenuItem selectedItem = getSelectedItem();
    if (selectedItem == null) {
      return;
    }
    try {
      controller.addToFavorites(selectedItem);
    } catch (RuntimeException failure) {
      showFailure("Could not add that item to favorites", failure);
      return;
    }
    refresh();
    statusLabel.setText("Added " + selectedItem.getName() + " to favorites");
  }

  // Handler methods reachable only from inside a listener, and not called by anything else in the panel.
  /**
   * Clears both filters and redraws the full sectioned menu. Resetting the
   * dropdown starts its own listener, so {@link #refresh()} runs once from that
   * and once from the explicit call here. Call is kept so the method is
   * correct on its own rather than relying on listener side effects.
   */
  private void handleShowAll() {
    searchField.setText("");
    categoryCombo.setSelectedItem(ALL_CATEGORIES_LABEL);
    refresh();
  }

  /**
   * Swaps the table for a prompt when filters find no match.
   * Early return is required since without it a repeat call would add the same component to the center slot twice.
   * Because the state is known to be flipping, the component being removed is always the one
   * currently in that slot.
   * @param isEmpty true when no items are showing
   */
  private void showEmptyState(boolean isEmpty) {
    if (isEmpty == showingEmptyState) {
      return;
    }
    remove(isEmpty ? tableScrollPane : emptyStateLabel);
    add(isEmpty ? emptyStateLabel : tableScrollPane, BorderLayout.CENTER);
    showingEmptyState = isEmpty;
    revalidate();
    repaint();
  }

  /** Enables the two action buttons only while a row is selected. */
  private void updateButtonState() {
    boolean hasSelection = menuTable.getSelectedRow() >= 0;
    addToCartButton.setEnabled(hasSelection);
    addToFavoritesButton.setEnabled(hasSelection);
  }

  /**
   * Recovers the model object behind the selected row. A table row holds only
   * display text, so the item is found by position in
   * @return the selected item, or null when nothing is selected
   */
  private MenuItem getSelectedItem() {
    int selectedRow = menuTable.getSelectedRow();
    if (selectedRow < 0 || selectedRow >= displayedItems.size()) {
      return null;
    }
    return displayedItems.get(selectedRow);
  }

  /**
   * Builds the line beneath the table describing what is currently showing.
   * The noun switches on whether a search is active, so results read as
   * "matches" and a plain browse reads as "items". Clause is appended
   * only when filter is actually applied.
   * @param itemCount how many items are showing
   * @param keyword the current search keyword and may be null or blank
   * @param category the active category, or null when none is applied
   * @return the status text, for example: 0 matches for "milk"
   */
  static String buildStatusText(int itemCount, String keyword, Category category) {
    boolean searching = isSearching(keyword);
    String noun;
    if (searching) {
      noun = (itemCount == 1) ? "match" : "matches";
    } else {
      noun = (itemCount == 1) ? "item" : "items";
    }
    String text = itemCount + " " + noun;
    if (searching) {
      text = text + " for \"" + keyword.trim() + "\"";
    }
    if (category != null) {
      text = text + " in " + ItemTableFormat.formatCategory(category);
    }
    return text;
  }

  /**
   * Picks the right controller call for the current filters then keyword narrowed category.
   * A category is filtered by the controller and doesn't give the full sectioned menu.
   * @param keyword the current search keyword, may be null or blank
   * @param category the active category, or null when none is applied
   * @return the items to display, never null
   */
  private List<MenuItem> resolveItems(String keyword, Category category) {
    List<MenuItem> items;
    if (isSearching(keyword)) {
      items = narrowToCategory(controller.search(keyword.trim()), category);
    } else if (category != null) {
      items = controller.filterByCategory(category);
    } else {
      items = flattenGrouped(controller.getGroupedMenu());
    }
    return (items == null) ? new ArrayList<MenuItem>() : items;
  }

  /**
   * Reports a failed controller call to the user. The controller wraps every
   * persistence failure in a {@code RuntimeException} and stating the cause.
   * @param summary a brief description of what failed
   * @param failure the exception that caused it
   */
  private void showFailure(String summary, RuntimeException failure) {
  JOptionPane.showMessageDialog(
          this, summary + ".\n" + failure.getMessage(),
          "Menu", JOptionPane.ERROR_MESSAGE);
  }
}
