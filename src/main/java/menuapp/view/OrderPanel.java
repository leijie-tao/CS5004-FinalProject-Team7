package menuapp.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import menuapp.controller.AppController;
import menuapp.model.MenuItem;
import menuapp.model.Order;

/**
 * Customer screen for cart items, quantities, total, and checkout. Every redraw reads the cat back from the controller.
 * Every button calls the controller and then redraws.
 * OrderPanel is broken down into 3 components.
 * 1.) Statics - This is the model and Strings
 * 2.) Private Helpers - These are the widgets {@code restoreSelection}, {@code updateButtonState},
 * and {@code getSelectedItemName}
 * 3.) Handlers - Consist of widgets and controller  {@code handleIncrease}, {@code handleDecrease},
 * {@code handleRemove}, {@code handleCheckout}
 */
public class OrderPanel extends AppPanel {
    /**
     * Shows how many distinct lines the cart holds
     */
    private final JLabel headerLabel;
    /**
     * Shown in place of the table when the cart is empty.
     */
    private final JLabel emptyStateLabel;
    /**
     * Read-only model, rebuilt wholesale on every redraw.
     */
    private final DefaultTableModel tableModel;
    /**
     * Shows the cart lines.
     */
    private final JTable cartTable;
    /**
     * Scroll container for the table, swapped out when the cart is empty.
     */
    private final JScrollPane tableScrollPane;
    /**
     * Lowers the selected line by one, or removes it at quantity one.
     */
    private final JButton decreaseButton;
    /**
     * Raises the selected line by one.
     */
    private final JButton increaseButton;
    /**
     * Removes the selected line outright.
     */
    private final JButton removeButton;
    /**
     * Confirms the cart.
     */
    private final JButton checkoutButton;
    /**
     * Shows the running total.
     */
    private final JLabel totalLabel;


    /**
     * Creates the cart screen.
     *
     * @param controller the shared controller
     */
    public OrderPanel(AppController controller) {
        super(controller, "Cart");

        this.headerLabel = new JLabel();
        this.emptyStateLabel = new JLabel(
                "Your cart is empty. Add something from the Menu tab.", SwingConstants.CENTER);
        this.tableModel = new ReadOnlyTableModel(cartColumnNames());
        this.cartTable = new JTable(tableModel);
        this.tableScrollPane = new JScrollPane(cartTable);
        this.decreaseButton = new JButton("\u2212");
        this.increaseButton = new JButton("+");
        this.removeButton = new JButton("Remove");
        this.checkoutButton = new JButton("Checkout");
        this.totalLabel = new JLabel();

        // Dependency chain -- Do not reorder.
        layOutComponents();
        attachListeners();
        refresh();
    }

    /**
     * Arranges the header, table, and control strip inside a border layout. The
     * table goes into the centre slot here, which is the precondition
     * {@link AppPanel#showEmptyState} relies on to swap it out later.
     */
    private void layOutComponents() {
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        add(headerLabel, BorderLayout.NORTH);

        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.setRowHeight(24);
        cartTable.getTableHeader().setReorderingAllowed(false);
        add(tableScrollPane, BorderLayout.CENTER);

        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 14f));

        JPanel controlStrip = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controlStrip.add(new JLabel("Quantity:"));
        controlStrip.add(decreaseButton);
        controlStrip.add(increaseButton);
        controlStrip.add(removeButton);
        controlStrip.add(checkoutButton);
        controlStrip.add(totalLabel);
        add(controlStrip, BorderLayout.SOUTH);
    }

    // Wiring of listeners and controls

    /**
     * Connects the table and buttons to the actions they should perform. Changing the selected row updates which buttons
     * are enabled, while each button calls its matching handler when clicked.
     */
    private void attachListeners() {
        cartTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                                                                   @Override
                                                                   public void valueChanged(ListSelectionEvent event) {
                                                                       updateButtonState();
                                                                   }
                                                               }
        );

        decreaseButton.addActionListener(new ActionListener() {
                                             @Override
                                             public void actionPerformed(ActionEvent event) {
                                                 handleDecrease();
                                             }
                                         }
        );

        increaseButton.addActionListener(new ActionListener() {
                                             @Override
                                             public void actionPerformed(ActionEvent event) {
                                                 handleIncrease();
                                             }
                                         }
        );

        removeButton.addActionListener(new ActionListener() {
                                           @Override
                                           public void actionPerformed(ActionEvent event) {
                                               handleRemove();
                                           }
                                       }
        );

        checkoutButton.addActionListener(new ActionListener() {
                                             @Override
                                             public void actionPerformed(ActionEvent event) {
                                                 handleCheckout();
                                             }
                                         }
        );
    }

    /**
     * Get the newest cart information and redraw everything on the screen without making the user lose
     * their selected row. It shows the empty cart screen if necessary and makes sure the buttons are
     * enabled/disabled correctly.
     */
    @Override
    public void refresh() {
        int previousRow = cartTable.getSelectedRow();
        Order currentCart;

        // guard block
        try {
            currentCart = controller.getCart();
        } catch (UnsupportedOperationException notBuiltYet) {
            showNotReady(tableScrollPane, "getCart");
            return;
        }
        Map<MenuItem, Integer> lines = currentCart.getItemsWithQuantities();
        int lineCount = (lines == null) ? 0 : lines.size();

        tableModel.setDataVector(buildCartRows(lines), cartColumnNames());
        headerLabel.setText(buildHeaderText(lineCount));
        totalLabel.setText(buildTotalText(currentCart.getTotal()));

        restoreSelection(previousRow);
        showEmptyState(lineCount == 0, tableScrollPane, emptyStateLabel);
        updateButtonState();
    }

    // Private Helpers

    /**
     * Reads the item name out of the selected row. A row holds only display text,
     * and every cart call is keyed by name, so no model object is needed.
     *
     * @return the selected item name, or null when nothing is selected
     */
    private String getSelectedItemName() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return String.valueOf(tableModel.getValueAt(selectedRow, CART_NAME_COLUMN));
    }

    /**
     * Reinstates a row selection after the table has been rebuilt.
     *
     * @param previousRow the row that was selected before the rebuild
     */
    private void restoreSelection(int previousRow) {
        int row = clampSelection(previousRow, tableModel.getRowCount());
        if (row >= 0) {
            cartTable.setRowSelectionInterval(row, row);
        }
    }

    /**
     * Enables only the buttons that make sense for the current cart and selection.
     */
    private void updateButtonState() {
        boolean hasSelection = cartTable.getSelectedRow() >= 0;
        decreaseButton.setEnabled(hasSelection);
        increaseButton.setEnabled(hasSelection);
        removeButton.setEnabled(hasSelection);
        checkoutButton.setEnabled(tableModel.getRowCount() > 0);
    }

    // Handlers that identify target, call the controller, handles exception if arises, and refresh()

    /**
     * Raises the selected line by one, then redraws. The current quantity is read
     * back from the controller rather than off the table, so the increment is
     * applied to what the cart actually holds.
     */
    private void handleIncrease() {
        String itemName = getSelectedItemName();
        if (itemName == null) {
            return;
        }
        int quantity = quantityOf(controller.getCart().getItemsWithQuantities(), itemName);
        if (quantity <= 0) {
            refresh();
            return;
        }
        try {
            controller.setCartQuantity(itemName, quantity + 1);
        } catch (RuntimeException failure) {
            showFailure("Could not change that quantity", failure);
        }
        refresh();
    }

    /**
     * Lowers the selected line by one, then redraws. At quantity one the line is
     * removed instead, because {@code Order.setQuantity} throws on zero.
     */
    private void handleDecrease() {
        String itemName = getSelectedItemName();
        if (itemName == null) {
            return;
        }
        int quantity = quantityOf(controller.getCart().getItemsWithQuantities(), itemName);
        if (quantity <= 0) {
            refresh();
            return;
        }
        try {
            if (shouldRemoveOnDecrease(quantity)) {
                controller.removeFromCart(itemName);
            } else {
                controller.setCartQuantity(itemName, quantity - 1);
            }
        } catch (RuntimeException failure) {
            showFailure("Could not change that quantity", failure);
        }
        refresh();
    }

    /**
     * Removes the selected line outright, then redraws.
     */
    private void handleRemove() {
        String itemName = getSelectedItemName();
        if (itemName == null) {
            return;
        }
        try {
            controller.removeFromCart(itemName);
        } catch (RuntimeException failure) {
            showFailure("Could not remove that item", failure);
        }
        refresh();
    }

    /**
     * Asks for confirmation, then confirms the cart. The redraw runs whether the
     * checkout succeeded or failed: on success the cart is empty, on failure it
     * is unchanged, and the screen must show whichever happened.
     */
    private void handleCheckout() {
        if (tableModel.getRowCount() == 0) {
            return;
        }
        int answer = JOptionPane.showConfirmDialog(
                this, "Place this order for " + totalLabel.getText() + "?",
                "Checkout", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controller.checkout();
        } catch (RuntimeException failure) {
            showFailure("Could not place that order", failure);
            refresh();
            return;
        }
        refresh();
    }

    // Statics that are either formatters or creates decisions and usually hides inside a listener

    /**
     * Column headers for the cart table. Category is dropped and quantity and
     * subtotal are added.
     */
    private static final String[] CART_COLUMN_NAMES = {"Item", "Price", "Qty", "Subtotal"};

    /**
     * Index of the item name column, the column that identifies a selected row.
     */
    static final int CART_NAME_COLUMN = 0;

    /**
     * Returns the cart column headers as a fresh copy on every call.
     *
     * @return a new array holding the four headers
     */
    static String[] cartColumnNames() {
        return CART_COLUMN_NAMES.clone();
    }

    /**
     * Converts cart lines into the row data the table displays. A linked hash map remembers the order things were added
     * so if an item does get added (i.e., burger, fries, sprite --> table would show: Burger, Fries, Sprite) to prevent
     * shuffling after every redraw.
     * The subtotal multiplies the unrounded price and formats the product instead of multiplying the
     * rounded display texts. An item priced at 6.601 therefore shows a unit price of $6.60
     * and a subtotal of $66.01 at quantity ten.
     *
     * @param cart each item paired with its quantity and may be null
     * @return one row per line holding name, unit price, quantity, and subtotal
     */
    static Object[][] buildCartRows(Map<MenuItem, Integer> cart) {
        if (cart == null) {
            return new Object[0][CART_COLUMN_NAMES.length];
        }
        Object[][] rows = new Object[cart.size()][CART_COLUMN_NAMES.length];
        int rowIndex = 0;
        for (Map.Entry<MenuItem, Integer> line : cart.entrySet()) {
            MenuItem item = line.getKey();
            int quantity = line.getValue();
            rows[rowIndex][0] = item.getName();
            rows[rowIndex][1] = ItemTableFormat.formatPrice(item.getPrice());
            rows[rowIndex][2] = String.valueOf(quantity);
            rows[rowIndex][3] = ItemTableFormat.formatPrice(item.getPrice() * quantity);
            rowIndex++;
        }
        return rows;
    }

    /**
     * Builds the header line above the table. It counts distinct lines rather than
     * total units, so one item is read instead of three (if there are three present).
     *
     * @param lineCount how many distinct items the cart holds
     * @return the header text
     */
    static String buildHeaderText(int lineCount) {
        String unit = (lineCount == 1) ? "item" : "items";
        return "Cart (" + lineCount + " " + unit + ")";
    }

    /**
     * Builds the running total line beneath the table.
     *
     * @param total the cart total in dollars
     * @return the total text, for example {@code Total: $37.00}
     */
    static String buildTotalText(double total) {
        return "Total: " + ItemTableFormat.formatPrice(total);
    }

    /**
     * Looks through the cart for an item with a matching name and return its quantity.
     * If the cart, name or item doesn't exist,then it returns a 0 instead of causing an error.
     *
     * @param cart each item paired with its quantity, may be null
     * @param name the item name to look for, may be null
     * @return the quantity on that line, or zero when it is not in the cart
     */
    static int quantityOf(Map<MenuItem, Integer> cart, String name) {
        if (cart == null || name == null) {
            return 0;
        }
        for (Map.Entry<MenuItem, Integer> line : cart.entrySet()) {
            if (line.getKey().getName().equals(name)) {
                return line.getValue();
            }
        }
        return 0;
    }

    /**
     * If the quantity is 1 or less than 1, returns true. Otherwise, remove the item from the cart instead
     * of reducing quantity.There should be no negative or 0, and if it does occur, item is removed;.
     *
     * @param currentQuantity the quantity showing on the line
     * @return true when the line should be removed
     */
    static boolean shouldRemoveOnDecrease(int currentQuantity) {
        return currentQuantity <= 1;
    }

    /**
     * Figures which row should stay selected after the table refreshes by trying to keep the same row highlighted. If
     * the row no longer exists, then the last available row is selected instead. If there are no rows or nothing was
     * selected before , then it selects nothing.
     *
     * @param previousRow the row index selected before the rebuild. Set to -1 when nothing was selected
     * @param rowCount    how many rows the table holds after the rebuild
     * @return the row index to select, or -1 to select nothing
     */
    static int clampSelection(int previousRow, int rowCount) {
        return ItemTableFormat.clampSelection(previousRow, rowCount);

    }
}