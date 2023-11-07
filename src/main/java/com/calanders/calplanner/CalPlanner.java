package com.calanders.calplanner;

import com.calanders.calplanner.calendar.gui.Calendar;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

/**
 * A class to create the CalPlanner window which houses the Calendar.
 */
public class CalPlanner {
    private final JFrame frame;
    private final Calendar calendar;

    /**
     * Constructs a new CalPlanner and creates a new JFrame window. This window will contain the
     * Calendar and is ready to be interacted with immediately.
     */
    public CalPlanner() {
        frame = new JFrame("CalPlanner");
        calendar = new Calendar();

        init();
    }

    private void init() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        frame.setIconImage(new ImageIcon(getClass().getResource("/icons/calplanner.png")).getImage());
        frame.setMinimumSize(new Dimension(960, 540));
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.add(calendar);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * The main method of the CalPlanner application. This method creates a new CalPlanner window
     * where the Calendar will be displayed.
     *
     * @param args the command line arguments array
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(CalPlanner::new);
    }
}
