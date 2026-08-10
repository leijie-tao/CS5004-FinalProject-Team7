package menuapp.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import menuapp.controller.AppController;
import menuapp.model.Category;
import menuapp.model.FavoritesList;
import menuapp.model.MenuItem;


/**
 * Customer screen for view, modify, save, and load a favorites list. 
 * Favorites do not feed the cart or checkout and it owns no domain state or store a copy of items
 * in a field, computes total, or modify a file directly. Buttons call the controller directly
 * and uses refresh(). This is a widget state only.
 */
public class FavoritesPanel extends AppPanel {

  /** Column headers for the favorites table. */
  private static final String[] COLUMN_NAMES = {"Item", "Category", "Price"};
  /** Index of the item-name column to identify a selected row. */
  private static final int NAME_COLUMN = 0;
  /** Shows the list label and how many items it holds. */
  private final JLabel headerLabel;
  /** Shown in place of the table when the list is empty. */
  private final JLabel emptyStateLabel;
  /** Default table that builds wholesale on every refresh. */
  private final DefaultTableModel tableModel;
  /** A read-only for the favorites table. Reminder that edits go through the controller. */
  private final JTable favoritesTable;
  /** Scroll container for the table while the list is empty. */
  private final JScrollPane tableScrollPane;
  /** Removes the currently selected item and is disabled when nothing is selected. */
  private final JButton removeButton;
  /** Renames the list. */
  private final JButton renameButton;
  /** Writes the list to a chosen file and disabled when the list is empty. */
  private final JButton saveButton;
  /** Reads list back from the chosen file. */
  private final JButton loadButton;
  /** Tracks which component currently in the center slot and starts false.*/
  private boolean showingEmptyState; // only mutable widget

  /**
   * Builds the favorites screen from current model state.
   * @param controller the shared controller
   */
  public FavoritesPanel(AppController controller) {
    super(controller, "Favorites");

    this.headerLabel = new JLabel();
    this.emptyStateLabel = new JLabel(
            "You have no favorites yet! Add items to see them here.", SwingConstants.CENTER);
    this.tableModel = new ReadOnlyTableModel(COLUMN_NAMES);
    this.favoritesTable = new JTable(tableModel);
    this.tableScrollPane = new JScrollPane(favoritesTable);
    this.removeButton = new JButton("Remove Item");
    this.renameButton = new JButton("Rename List");
    this.saveButton = new JButton("Save Favorites");
    this.loadButton = new JButton("Load Favorites");

    layOutComponents(); // widget must exist first
    attachListeners(); //
    refresh(); // note, might want to turn this into a final class
  }

  /** Arranges the header, table, and button row inside a border layout. */
  private void layOutComponents() {
    setLayout(new BorderLayout(0, 8));
    setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));
    headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
    add(headerLabel, BorderLayout.NORTH);

    favoritesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    favoritesTable.setRowHeight(24);
    favoritesTable.getTableHeader().setReorderingAllowed(false);
    add(tableScrollPane, BorderLayout.CENTER);

    JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    buttonRow.add(removeButton);
    buttonRow.add(renameButton);
    buttonRow.add(saveButton);
    buttonRow.add(loadButton);
    add(buttonRow, BorderLayout.SOUTH);
  }


  // Listeners
  /** Wires every control to a controller call followed by a redraw. */
  private void attachListeners() {
    favoritesTable.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
              @Override
              public void valueChanged(ListSelectionEvent event) {
                updateButtonState();
              }
            }
            );

    removeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        handleRemoveSelected();
      }
    }
    );

    renameButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        handleRename();
      }
    }
    );

    saveButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        handleSave();
      }
    }
    );

    loadButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        handleLoad();
      }
    }
    );
  }

// Table Rendering
  /**
   * Redraws the whole screen from current model. Reads fresh favorites list
   * from controller rather than caching it. Method is safe to call after any change.
   */
  @Override
  public void refresh() {
    FavoritesList currentFavorites;

    // guard block
    try {
      currentFavorites = controller.getFavorites();
    } catch (UnsupportedOperationException notBuiltYet) {
      showNotReady(tableScrollPane, "getFavorites");
      return;
    }
    List<MenuItem> items = currentFavorites.getItems();
    int itemCount = (items == null) ? 0 : items.size();

    tableModel.setDataVector(buildRows(items), COLUMN_NAMES);
    headerLabel.setText(buildHeaderText(currentFavorites.getName(), itemCount));

    showEmptyState(itemCount == 0, tableScrollPane, emptyStateLabel);
    updateButtonState();
  }

  /**
   * Converts menu items into the row data the table displays. This is a static method 
   * with no Swing dependencies so it can be unit tested without opening a window.
   * @param items the items to display and can be null
   * @return a row holding name, category, and formatted price
   */
  static Object[][] buildRows(List<MenuItem> items) {
    return ItemTableFormat.buildRows(items);
  }

  /**
   * Turns an enum constant into readable text so BEVERAGE reads as Beverage.
   * @param category the category to format
   * @return the display text for that category
   */
  static String formatCategory(Category category) {
    return ItemTableFormat.formatCategory(category);
  }

  /**
   * Builds the header line above the table.
   * @param listName the label of the list
   * @param itemCount how many items it holds
   * @return the header text, for example "My Favorites (4 items)"
   */
  static String buildHeaderText(String listName, int itemCount) {
    String safeName = (listName == null) ? "Favorites" : listName;
    String unit = (itemCount == 1) ? "item" : "items";
    return safeName + " (" + itemCount + " " + unit + ")";
  }

  /**
   * Swaps the table for a prompt when nothing is shown.
   * @param isEmpty true when the list holds no items
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

  /** Enables only the buttons that make sense for the current selection. */
  private void updateButtonState() {
    removeButton.setEnabled(favoritesTable.getSelectedRow() >= 0);
    saveButton.setEnabled(tableModel.getRowCount() > 0);
  }

  /**
   * Reads the item name out of the selected row.
   * @return the selected item name, or null when nothing is selected
   */
  private String getSelectedItemName() {
    int selectedRow = favoritesTable.getSelectedRow();
    if (selectedRow < 0) {
      return null;
    }
    return String.valueOf(tableModel.getValueAt(selectedRow, NAME_COLUMN));
  }

  /** Removes the selected item, then redraws. */
  private void handleRemoveSelected() {
    String itemName = getSelectedItemName();
    if (itemName == null) {
      return;
    }
    removeFavorite(itemName);
    refresh();
  }

  /** Asks for a new list label, applies it, then redraws. */
  private void handleRename() {
    String currentName = controller.getFavorites().getName();
    String newName = JOptionPane.showInputDialog(
            this, "Name for this list:", currentName);
    if (newName == null || newName.trim().isEmpty()) {
      return;
    }
    renameFavorites(newName.trim());
    refresh();
  }

  /** Asks where to write the list and hands the path to the controller. */
  private void handleSave() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save favorites");
    fileChooser.setSelectedFile(
            new File(controller.getFavorites().getName() + ".json"));
    if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
      return;
    }
    try {
      controller.saveFavorites(fileChooser.getSelectedFile().getAbsolutePath());
    } catch (RuntimeException failure) {
      showFailure("Could not save the list", failure);
    }
    refresh();
  }

  /** Asks which file to read and hands the path to the controller. */
  private void handleLoad() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Load favorites");
    if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
      return;
    }
    try {
      controller.loadFavorites(fileChooser.getSelectedFile().getAbsolutePath());
    } catch (RuntimeException failure) {
      showFailure("Could not load that file", failure);
    }
    refresh();
  }

  /**
   * Removes one item from the favorites list.
   * <p>TODO: replace the body with {@code controller.removeFromFavorites(itemName)}
   * once that method is added to {@link AppController}. Reaching through
   * {@code getFavorites()} to mutate the model is a temporary shortcut.
   * @param itemName the name of the item to remove
   */
  private void removeFavorite(String itemName) {
    controller.getFavorites().remove(itemName);
  }

  /**
   * Renames the favorites list.
   * <p>TODO: replace the body with {@code controller.renameFavorites(newName)}
   * once that method is added to {@link AppController}. Same temporary shortcut
   * as {@link #removeFavorite(String)}.
   * @param newName the new label for the list
   */
  private void renameFavorites(String newName) {
    controller.getFavorites().setName(newName);
  }


//  /** KEEPING BELOW AS REFERENCE. Delete in the main project
//  * Displays an error message when a save or load operation fails.
//  * @param summary a brief description of the failed operation
//  * @param failure the exception that caused the failure
//  */
//  private void showFailure(String summary, RuntimeException failure) {
//    JOptionPane.showMessageDialog(
//      this, summary + ".\n" + failure.getMessage(),
//      "Favorites", JOptionPane.ERROR_MESSAGE);
//    }
}