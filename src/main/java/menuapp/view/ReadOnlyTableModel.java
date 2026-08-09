package menuapp.view;

import javax.swing.table.DefaultTableModel;

/** Create empty read-only table model with given column names. This exists so that view never edits model */
public class ReadOnlyTableModel extends DefaultTableModel {
    ReadOnlyTableModel(Object[] columnNames) {
        super(new Object[0][columnNames.length], columnNames);
    }

    /** Shows is chosen cell can be edited
     * @param row which is the index of cell's row
     * param column index of cell's column
     * @return false because the controller owns all changes now view
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
