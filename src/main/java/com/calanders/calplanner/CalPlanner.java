package com.calanders.calplanner;

import com.calanders.calplanner.calendar.gui.Calendar;

import javax.swing.*;
import java.awt.*;
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
    }
}
