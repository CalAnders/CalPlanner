package com.calanders.calplanner.calendar.gui.table;

import com.calanders.calplanner.calendar.task.Task;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TaskRenderer extends JLabel implements TableCellRenderer {
    public TaskRenderer() {}

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Task task = (Task)value;

        if (task != null) {
            String text = task.getText() + ", " + task.getTime();
            table.setValueAt(text, 0, 0);

            System.out.println(text + ", at: " + row + " " +  column);
        }

        return this;
    }
}
