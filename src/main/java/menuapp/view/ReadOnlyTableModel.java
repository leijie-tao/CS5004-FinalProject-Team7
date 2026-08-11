package menuapp.view;

import javax.swing.table.DefaultTableModel;

/**
 * A table model whose cells can never be edited in place. Every panel that shows a table uses this
 * so that the View cannot write to the Model. Without it a user could type a new price straight into a cell and the
 * display would disagree with the Model with no controller call in between.
 */
class ReadOnlyTableModel extends DefaultTableModel {

    /**
     * Creates a table with the given column headers and no rows.
     * The rows are added later, each time the panel refreshes.
     *
     * @param columnNames the column header in the order they appear
     */
    ReadOnlyTableModel(Object[] columnNames) {
        super(new Object[0][columnNames.length], columnNames);
    }

    /**
     * Reports whether a cell can be edited by typing into it. Always false.
     *
     * @param row    the index of the cell's row
     * @param column the index of the cell's column
     * @return false, because the controller owns every change, not the View
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}