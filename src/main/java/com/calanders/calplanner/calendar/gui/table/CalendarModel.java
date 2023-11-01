package com.calanders.calplanner.calendar.gui.table;

import com.calanders.calplanner.calendar.task.Task;

import javax.swing.table.DefaultTableModel;

public class CalendarModel extends DefaultTableModel {
    private Object[] columnNames;
    private int rowCount;

    public CalendarModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
        this.columnNames = columnNames;
        this.rowCount = rowCount;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Task.class;
    }
}
