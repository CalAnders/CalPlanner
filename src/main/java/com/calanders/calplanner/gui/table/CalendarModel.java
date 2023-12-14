package com.calanders.calplanner.gui.table;

import javax.swing.table.DefaultTableModel;

/**
 * A class that represents the model of the Calendar.
 */
public class CalendarModel extends DefaultTableModel {
    /**
     * Constructs a new CalendarModel with an array of column names and the number of rows in the model.
     *
     * @param columnNames the names of the columns
     * @param rowCount the number of rows
     */
    public CalendarModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    /**
     * Sets the cells to be uneditable so any cell manipulation must be done via new Task, edit Task,
     * and delete Task.
     *
     * @param row the row whose value is to be queried
     * @param column the column whose value is to be queried
     * @return false since the cells in CalendarModel are not editable
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
