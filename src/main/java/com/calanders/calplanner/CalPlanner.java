package com.calanders.calplanner;

import com.calanders.calplanner.calendar.gui.Calendar;
import com.calanders.calplanner.calendar.gui.util.CellLocation;
import com.calanders.calplanner.calendar.task.Task;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

public class CalPlanner {
    private final JFrame frame;
    private final Calendar calendar;

    public CalPlanner() {
        frame = new JFrame("CalPlanner");
        calendar = new Calendar();

        init();
    }

    private void init() {
        frame.setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/calplanner.png"))).getImage());
        frame.setMinimumSize(new Dimension(960, 540));
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.add(calendar);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(CalPlanner::new);

        CellLocation one = new CellLocation(0, 0);
        CellLocation two = new CellLocation(1, 0);

        HashMap<CellLocation, Task> taskMap = new HashMap<>();
        taskMap.put(one, new Task("cell 0,0", "SUNDAY 10-29", "11:59 PM", 1));
        taskMap.put(one, new Task("cell 0,0", "SATURDAY 10-28", "11:59 PM", 0));
        taskMap.put(two, new Task("cell 1,0", "SUNDAY 10-29", "11:59 PM", 2));

        System.out.println(taskMap.get(one));
    }
}
