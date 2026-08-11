package menuapp.view;

import menuapp.controller.AppController;
import menuapp.model.Inventory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Staff screen for the stock table, restock controls, and exporting the low stock sub-list to JSON.
 * Follows through with OrderPanel's three tier of static, private helpers, and handlers.
 * 1. Static - Located at the foot of this class, which turn model values and typed text
 * into display text and touch no widget.
 * 2. Private Helpers - These private helpers read and write widgets.
 * 3. Handlers - These identify a target, call the controller, and redraw.
 */
public class InventoryPanel extends AppPanel {
    /**
     * Shows how many items carry a stock entry.
     */
    private final JLabel headerLabel;
    /**
     * Shown in place of the two tables when nothing is stocked at all.
     */
    private final JLabel emptyStateLabel;
    /**
     * Holds both tables. This is the swappable center region.
     */
    private final JPanel contentPanel;
    /**
     * Read-only model behind the full stock table, rebuilt wholesale on every redraw.
     */
    private final DefaultTableModel stockTableModel;
    /**
     * Shows every stocked item. The only table a restock target is chosen from.
     */
    private final JTable stockTable;
    /**
     * Scroll container for the stock table.
     */
    private final JScrollPane stockScrollPane;
    /**
     * Names the threshold the sub-list was built from, and how many items matched.
     */
    private final JLabel lowStockHeaderLabel;
    /**
     * Read-only model behind the low stock sub-list.
     */
    private final DefaultTableModel lowStockTableModel;
    /**
     * Shows the sub-list. Display only, so the restock target is never ambiguous.
     */
    private final JTable lowStockTable;
    /**
     * Scroll container for the sub-list table.
     */
    private final JScrollPane lowStockScrollPane;
    /**
     * Where the staff type the threshold to build the sub-list from.
     */
    private final JTextField thresholdField;
    /**
     * Applies whatever the threshold field currently holds.
     */
    private final JButton applyThresholdButton;
    /**
     * Writes the current sub-list to a JSON file.
     */
    private final JButton exportButton;
    /**
     * Where the staff type how many units to add to the selected item.
     */
    private final JTextField restockAmountField;
    /**
     * Adds that many units to the selected item.
     */
    private final JButton restockButton;

    /**
     * The threshold value currently applied to the list shown on screen.The list is not stored here.
     * The controller uses this value whenever it needs to get the list again. This value only changes
     * when the user clicks Apply, so it may not match the value currently typed in {@link #thresholdField}.
     */
    private int activeThreshold;

    /**
     * Creates the inventory screen.
     *
     * @param controller the shared controller
     */
    public InventoryPanel(AppController controller) {
        super(controller, SCREEN_TITLE);
        this.activeThreshold = DEFAULT_THRESHOLD;
        this.headerLabel = new JLabel();
        this.emptyStateLabel = new JLabel("Nothing is stocked yet.", SwingConstants.CENTER);
        this.contentPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        this.stockTableModel = new ReadOnlyTableModel(stockColumnNames());
        this.stockTable = new JTable(stockTableModel);
        this.stockScrollPane = new JScrollPane(stockTable);
        this.lowStockHeaderLabel = new JLabel();
        this.lowStockTableModel = new ReadOnlyTableModel(stockColumnNames());
        this.lowStockTable = new JTable(lowStockTableModel);
        this.lowStockScrollPane = new JScrollPane(lowStockTable);
        this.thresholdField = new JTextField(String.valueOf(DEFAULT_THRESHOLD), 4);
        this.applyThresholdButton = new JButton("Apply");
        this.exportButton = new JButton("Export sub-list\u2026");
        this.restockAmountField = new JTextField(4);
        this.restockButton = new JButton("Restock selected item");

        // Dependency chain -- Do not reorder.
        layOutComponents();
        attachListeners();
        refresh();
    }

    /**
     * Sets up the layout of the panel where the header is placed at the top, the two tables are placed side by side
     * in the center, and the restock controls are placed at the bottom. The table container is added to
     * the center so that showEmptyState can replace it later if needed.
     */
    private void layOutComponents() {
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        add(headerLabel, BorderLayout.NORTH);

        stockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stockTable.setRowHeight(24);
        stockTable.getTableHeader().setReorderingAllowed(false);

        // This is for Display only with 1 selectable table to show the restock target
        lowStockTable.setRowSelectionAllowed(false);
        lowStockTable.setRowHeight(24);
        lowStockTable.getTableHeader().setReorderingAllowed(false);

        contentPanel.add(stockScrollPane);
        contentPanel.add(buildLowStockSide());
        setCenter(contentPanel);

        JPanel restockStrip = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        restockStrip.add(new JLabel("Enter Restock amount:"));
        restockStrip.add(restockAmountField);
        restockStrip.add(restockButton);
        add(restockStrip, BorderLayout.SOUTH);
    }

    /**
     * Builds the right side of the panel for displaying low-stock items. This section contains the threshold controls,
     * the low-stock table, and the export button.
     *
     * @return the completed panel for the low-stock section
     */
    private JPanel buildLowStockSide() {
        JPanel thresholdStrip = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        thresholdStrip.add(new JLabel("Threshold:"));
        thresholdStrip.add(thresholdField);
        thresholdStrip.add(applyThresholdButton);

        lowStockHeaderLabel.setFont(lowStockHeaderLabel.getFont().deriveFont(Font.BOLD, 13f));

        JPanel topOfSide = new JPanel(new GridLayout(2, 1, 0, 4));
        topOfSide.add(thresholdStrip);
        topOfSide.add(lowStockHeaderLabel);

        JPanel exportStrip = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        exportStrip.add(exportButton);

        JPanel side = new JPanel(new BorderLayout(0, 4));
        side.add(topOfSide, BorderLayout.NORTH);
        side.add(lowStockScrollPane, BorderLayout.CENTER);
        side.add(exportStrip, BorderLayout.SOUTH);
        return side;
    }

    /**
     * Connects the table and controls to the actions they perform. Changing the
     * selected row updates which controls are enabled. Each button calls its
     * handler, and each text field calls the same handler as the button beside
     * it, so pressing Enter after typing does what it looks like it should.
     */
    private void attachListeners() {
        stockTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                updateButtonState();
            }
        });

        restockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                handleRestock();
            }
        });

        restockAmountField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                handleRestock();
            }
        });

        applyThresholdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                handleApplyThreshold();
            }
        });

        thresholdField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                handleApplyThreshold();
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                handleExport();
            }
        });
    }

    /**
     * Refreshes the screen with the latest inventory and low-stock data.
     * Both tables, their headers, and the button states are updated. The method also tries to keep the
     * same stock row selected after the refresh. If either controller method is not implemented yet,
     * the screen shows which feature is unavailable instead of causing an error.
     */
    @Override
    public void refresh() {
        int previousRow = stockTable.getSelectedRow();

        Map<String, Integer> stock;
        try {
            Inventory inventory = controller.getInventory();
            stock = (inventory == null) ? null : inventory.getAllStock();
        } catch (UnsupportedOperationException notBuiltYet) {
            showNotReady("getInventory"); // guard in case implmentation hits a block
            return;
        }

        List<String> lowStockNames;
        try {
            lowStockNames = controller.getLowStockItems(activeThreshold);
        } catch (UnsupportedOperationException notBuiltYet) {
            showNotReady("getLowStockItems"); // guard in case implmentation hits a block
            return;
        }

        int itemCount = (stock == null) ? 0 : stock.size();
        int lowStockCount = (lowStockNames == null) ? 0 : lowStockNames.size();

        stockTableModel.setDataVector(buildStockRows(stock), stockColumnNames());
        lowStockTableModel.setDataVector(buildLowStockRows(lowStockNames, stock), stockColumnNames());
        headerLabel.setText(buildHeaderText(itemCount));
        lowStockHeaderLabel.setText(buildLowStockHeaderText(activeThreshold, lowStockCount));

        restoreSelection(previousRow);
        showEmptyState(itemCount == 0, contentPanel, emptyStateLabel);
        updateButtonState();
    }

    // Private Helpers Section

    /**
     * Reads the item name out of the selected row. A row holds only display
     * text, and every inventory call is keyed by name, so no model object is
     * needed.
     *
     * @return the selected item name, or null when nothing is selected
     */
    private String getSelectedItemName() {
        int selectedRow = stockTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return String.valueOf(stockTableModel.getValueAt(selectedRow, STOCK_NAME_COLUMN));
    }

    /**
     * Reinstates a row selection after the stock table has been rebuilt.
     *
     * @param previousRow the row that was selected before the rebuild
     */
    private void restoreSelection(int previousRow) {
        int row = ItemTableFormat.clampSelection(previousRow, stockTableModel.getRowCount());
        if (row >= 0) {
            stockTable.setRowSelectionInterval(row, row);
        }
    }

    /**
     * Enables only the controls that make sense right now. Restocking needs a
     * selected item, and exporting needs a sub-list with something in it.
     */
    private void updateButtonState() {
        restockButton.setEnabled(stockTable.getSelectedRow() >= 0);
        exportButton.setEnabled(lowStockTableModel.getRowCount() > 0);
    }

    // Handlers whose role are to identify a target, validate input, call the controller, and redraw

    /**
     * Adds the typed number of units to the selected item, then redraws. Rejected input stops here rather than
     * reaching the controller, and leaves the screen untouched: nothing changed, so there is nothing to
     * redraw. On success the amount field is cleared, so the next restock starts from empty rather than silently
     * reusing the last number.
     */
    private void handleRestock() {
        String itemName = getSelectedItemName();
        if (itemName == null) {
            return;
        }
        String typedAmount = restockAmountField.getText();
        int amount = parseIntAtLeast(typedAmount, RESTOCK_MINIMUM);
        if (amount == INVALID_NUMBER) {
            showInvalidNumber("Restock amount", typedAmount, RESTOCK_MINIMUM);
            return;
        }
        try {
            controller.restock(itemName, amount);
            restockAmountField.setText("");
        } catch (RuntimeException failure) {
            showFailure("Sorry, could not restock that item", failure);
        }
        refresh();
    }

    /**
     * Commits whatever the threshold field holds as the criterion the sub-list
     * is built from, then redraws. The controller is not called here. Committing the criterion and then
     * redrawing is enough, because {@code refresh} is the one place that asks
     * for the sub-list.
     */
    private void handleApplyThreshold() {
        String typedThreshold = thresholdField.getText();
        int threshold = parseIntAtLeast(typedThreshold, THRESHOLD_MINIMUM);
        if (threshold == INVALID_NUMBER) {
            showInvalidNumber("Threshold", typedThreshold, THRESHOLD_MINIMUM);
            return;
        }
        activeThreshold = threshold;
        refresh();
    }

    /**
     * Asks where to write the sub-list and hands the path to the controller. The threshold sent
     * is {@link #activeThreshold}, the same one the header names, so the exported file always matches what
     * the screen was showing rather than whatever was last typed into the field.
     */
    private void handleExport() {
        if (lowStockTableModel.getRowCount() == 0) {
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export low stock list");
        fileChooser.setSelectedFile(new File("low-stock-" + activeThreshold + JSON_EXTENSION));
        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String filePath = ensureJsonExtension(fileChooser.getSelectedFile().getAbsolutePath());
        try {
            controller.exportLowStock(activeThreshold, filePath);
        } catch (RuntimeException failure) {
            showFailure("Sorry, could not export the low stock list", failure);
        }
        refresh();
    }

    /**
     * Tells the user their typed number was rejected, and why. This is a warning rather than {@code showFailure},
     * because nothing was attempted and nothing failed. {@code showFailure} needs a {@code RuntimeException} to
     * report, and manufacturing one to describe a typo would be using an exception for ordinary control flow.
     *
     * @param fieldLabel the name of the field, as the user sees it
     * @param rawText    the text that was rejected
     * @param minimum    the floor that was applied
     */
    private void showInvalidNumber(String fieldLabel, String rawText, int minimum) {
        JOptionPane.showMessageDialog(this, buildInvalidNumberMessage(fieldLabel, rawText, minimum), SCREEN_TITLE, JOptionPane.WARNING_MESSAGE);
    }


    // Statics Section: display text and decisions about values, with no widget in sight
    /**
     * Column headers shared by the stock table and the low stock sub-list table.
     */
    private static final String[] STOCK_COLUMN_NAMES = {"Item", "In stock"};

    /**
     * Index of the item name column, the column that identifies a selected row.
     */
    static final int STOCK_NAME_COLUMN = 0;

    /**
     * Value returned by {@link #parseIntAtLeast} when the input is not a valid number.
     * Since valid numbers must be zero or greater, -1 can safely represent an invalid result.
     */
    static final int INVALID_NUMBER = -1;

    /**
     * The default threshold used when the screen first opens.
     * The default is 5 because the sample inventory contains one item with only 2 units in stock.
     * This allows the low-stock feature to show an example immediately.
     */
    static final int DEFAULT_THRESHOLD = 5;

    /**
     * Extension the export is guaranteed to carry, since the file must be JSON.
     */
    private static final String JSON_EXTENSION = ".json";

    /**
     * Returns the column headers for a stock table as a fresh copy on every call.
     *
     * @return a new array holding the two headers, in display order
     */
    static String[] stockColumnNames() {
        return STOCK_COLUMN_NAMES.clone();
    }

    /**
     * Creates the rows used to display the inventory in a table. The item names
     * are sorted before being added to the table.
     *
     * @param stock the item names and their current stock amounts
     * @return the inventory data as rows for the table
     */
    static Object[][] buildStockRows(Map<String, Integer> stock) {
        if (stock == null) {
            return new Object[0][STOCK_COLUMN_NAMES.length];
        }
        List<String> names = new ArrayList<String>(stock.keySet());
        Collections.sort(names);

        Object[][] rows = new Object[names.size()][STOCK_COLUMN_NAMES.length];
        for (int rowIndex = 0; rowIndex < names.size(); rowIndex++) {
            String name = names.get(rowIndex);
            rows[rowIndex][0] = name;
            rows[rowIndex][1] = String.valueOf(stockOf(stock, name));
        }
        return rows;
    }

    /**
     * Creates table rows for items that are low in stock. If an item's stock
     * amount is missing, it is shown as zero.
     *
     * @param lowStockNames the names of the low-stock items
     * @param stock         the items and their current stock amounts
     * @return the low-stock items as rows for the table
     */
    static Object[][] buildLowStockRows(List<String> lowStockNames, Map<String, Integer> stock) {
        if (lowStockNames == null) {
            return new Object[0][STOCK_COLUMN_NAMES.length];
        }
        Object[][] rows = new Object[lowStockNames.size()][STOCK_COLUMN_NAMES.length];
        for (int rowIndex = 0; rowIndex < lowStockNames.size(); rowIndex++) {
            String name = lowStockNames.get(rowIndex);
            rows[rowIndex][0] = name;
            rows[rowIndex][1] = String.valueOf(stockOf(stock, name));
        }
        return rows;
    }

    /**
     * Reads one count out of a stock snapshot.
     *
     * @param stock the snapshot, may be null
     * @param name  the item to look up
     * @return the units in stock, or zero when the snapshot or the entry is absent
     */
    private static int stockOf(Map<String, Integer> stock, String name) {
        if (stock == null || !stock.containsKey(name)) {
            return 0;
        }
        return stock.get(name);
    }

    /**
     * Builds the header line above the stock table.
     *
     * @param itemCount how many items carry a stock entry
     * @return the header text for the inventory table
     */
    static String buildHeaderText(int itemCount) {
        return "Inventory (" + itemCount + " " + itemWord(itemCount) + ")";
    }

    /**
     * Creates the text shown above the low-stock list.
     *
     * @param threshold the stock limit used to find low-stock items
     * @param itemCount the number of low-stock items
     * @return the header text for the low-stock list
     */
    static String buildLowStockHeaderText(int threshold, int itemCount) {
        return "Low stock at or below " + threshold + " (" + itemCount + " " + itemWord(itemCount) + ")";
    }

    /**
     * Picks the singular or plural noun for a count.
     *
     * @param count the number being described
     * @return {@code item} at exactly one, {@code items} otherwise
     */
    private static String itemWord(int count) {
        return (count == 1) ? "item" : "items";
    }

    /**
     * Converts the user's text into a whole number. The number must be equal to or greater than the minimum value.
     *
     * @param text    the text entered by the user
     * @param minimum the smallest number allowed, must be zero or more
     * @return the number, or an INVALID_NUMBER if the input is not valid
     */
    static int parseIntAtLeast(String text, int minimum) {
        if (text == null) {
            return INVALID_NUMBER;
        }
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return INVALID_NUMBER;
        }
        int value;
        try {
            value = Integer.parseInt(trimmed);
        } catch (NumberFormatException notANumber) {
            return INVALID_NUMBER;
        }
        if (value < minimum) {
            return INVALID_NUMBER;
        }
        return value;
    }

    /**
     * Creates an error message when the user enters an invalid number.
     *
     * @param fieldLabel the name of the input field
     * @param rawText    the text entered by the user
     * @param minimum    the smallest number allowed
     * @return the error message to show the user
     */
    static String buildInvalidNumberMessage(String fieldLabel, String rawText, int minimum) {
        String rule = fieldLabel + " must be a whole number of at least " + minimum + ".";
        // check that doubles are okay
        if (rawText == null || rawText.trim().isEmpty()) {
            return rule;
        }
        return rule + " Got: \"" + rawText.trim() + "\".";
    }

    /**
     * Makes sure the file name ends with .json. If the path is empty or missing, it is returned without changes.
     *
     * @param path the file path entered by the user
     * @return the file path with .json added when needed
     */
    static String ensureJsonExtension(String path) {
        if (path == null) {
            return null;
        }
        String trimmed = path.trim();
        if (trimmed.isEmpty()) {
            return path;
        }
        if (trimmed.toLowerCase(Locale.US).endsWith(JSON_EXTENSION)) {
            return trimmed;
        }
        return trimmed + JSON_EXTENSION;
    }

    /**
     * Title on this screen's dialogs, both the base class's and this panel's own.
     */
    private static final String SCREEN_TITLE = "Inventory";

    /**
     * Lowest legal restock amount. {@code Inventory.increase} throws below this.
     */
    private static final int RESTOCK_MINIMUM = 1;

    /**
     * Lowest legal threshold. Zero is meaningful: it asks what is completely out.
     */
    private static final int THRESHOLD_MINIMUM = 0;
}