package com.calanders.calplanner.gui;

import com.calanders.calplanner.data.Task;
import com.calanders.calplanner.data.resources.Resources;
import com.calanders.calplanner.util.JTextFieldLimiter;
import com.calanders.calplanner.util.Util;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A class that serves to provide a Task creation and modification graphical user interface for Tasks
 * added to a Calendar. The TaskMenu can generate a new Task based on user input and selections
 * which will be added to the Calendar upon submission. Also, a TaskMenu can edit an existing
 * Task if supplied with a Task object.
 */
public class TaskMenu {
    private final JFrame frame;
    private final Calendar calendar;
    private final JPanel panel;
    private final JPanel formPanel;
    private String[] dates;
    private final String[] times;
    private final String[] priorities;
    private final JTextField text;
    private final JComboBox<String> date;
    private final JComboBox<String> time;
    private final JComboBox<String> priority;
    private final JButton submit;
    private boolean isEditing;
    private Task editingTask;

    /**
     * Constructs a new TaskMenu with a Calendar to create and add Tasks to.
     *
     * @param calendar the Calendar to create Tasks for
     */
    public TaskMenu(Calendar calendar) {
        this.calendar = calendar;
        frame = new JFrame("New Task");
        panel = new JPanel();
        formPanel = new JPanel();
        dates = calendar.getWeekDates();
        times = getTimes();
        priorities = new String[]{"Priority: Low", "Priority: Medium", "Priority: High"};
        text = new JTextField();
        date = createJComboBox(dates, calendar.getCurrentDayOfWeek().getValue() - 1);
        time = createJComboBox(times, 0);
        priority = createJComboBox(priorities, 1);
        submit = createSubmitButton();
        isEditing = false;

        init();
    }

    private void init() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        frame.setIconImage(Resources.CALENDAR_ICON);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        panel.setLayout(new GridBagLayout());
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints formConstraints = new GridBagConstraints();
        formConstraints.anchor = GridBagConstraints.CENTER;
        formConstraints.insets = new Insets(10, 10, 10, 10);
        formConstraints.ipadx = 10;
        formConstraints.ipady = 10;
        formConstraints.fill = GridBagConstraints.BOTH;
        formConstraints.gridwidth = 3;
        formConstraints.gridx = 0;
        formConstraints.gridy = 0;
        text.setToolTipText("Enter task name");
        text.setDocument(new JTextFieldLimiter(60));
        formPanel.add(text, formConstraints);
        formConstraints.gridwidth = 1;
        formConstraints.gridy++;
        formPanel.add(date, formConstraints);
        formConstraints.gridx++;
        formPanel.add(time, formConstraints);
        formConstraints.gridx++;
        formPanel.add(priority, formConstraints);
        panel.add(formPanel);

        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.insets = new Insets(10, 10, 10, 10);
        panelConstraints.ipadx = 10;
        panelConstraints.ipady = 10;
        panelConstraints.fill = GridBagConstraints.BOTH;
        panel.add(submit, panelConstraints);

        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    submitTask();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    frame.dispose();
                }
            }
        });

        frame.add(panel);
        frame.pack();
        frame.setVisible(false);
    }

    /**
     * Displays the TaskMenu to create a new Task. This will display the necessary options to create
     * a new Task as a graphical user interface.
     */
    public void displayCreator() {
        isEditing = false;
        dates = calendar.getWeekDates();
        text.setText(null);
        resetJComboBox(date, dates, calendar.getCurrentDayOfWeek().getValue() - 1);
        resetJComboBox(time, times, 0);
        resetJComboBox(priority, priorities, Task.PRIORITY_MEDIUM);
        submit.setText("Create Task");
        frame.setTitle("New Task");
        frame.setVisible(true);
    }

    /**
     * Displays the TaskMenu with options populated based on the Task argument.
     *
     * @param task the Task to edit
     */
    public void displayEditor(Task task) {
        editingTask = task;
        isEditing = true;
        dates = calendar.getWeekDates();
        text.setText(task.getText());
        resetJComboBox(date, dates, Util.indexOf(task.getDate(), calendar.getWeekDates()));
        resetJComboBox(time, times, Util.indexOf(task.getTime(), times));
        resetJComboBox(priority, priorities, task.getPriority());
        submit.setText("Update Task");
        frame.setTitle("Edit Task");
        frame.setVisible(true);
    }

    private void submitTask() {
        Task task = new Task(text.getText(),
                Objects.requireNonNull(date.getSelectedItem()).toString(),
                Objects.requireNonNull(time.getSelectedItem()).toString(),
                priority.getSelectedIndex());

        if (!task.getText().isEmpty()) {
            if (isEditing) {
                task.setUUID(editingTask.getUUID());
                calendar.editTask(task);
            } else {
                calendar.addTask(task);
            }
            frame.dispose();
        }
    }

    /**
     * Sets the location of the Window to the specified Point. The parameter p determines the
     * top-left corner of the Window.
     *
     * @param p the top-left Point of the Window
     */
    public void setLocation(Point p) {
        frame.setLocation(p);
    }

    /**
     * Sets the location of the Window relative to the specified Component. If the argument is
     * null, the default location will be the center of the screen relative to the center of the
     * Window instead of the top-left corner.
     *
     * @param c the relative Component
     */
    public void setLocationRelativeTo(Component c) {
        frame.setLocationRelativeTo(c);
    }

    /**
     * Retrieves the size of the Window defined by a Dimension object.
     *
     * @return the Dimension of the Window
     */
    public Dimension getSize() {
        return frame.getSize();
    }

    /**
     * Sets the text in the text field to the specified String.
     *
     * @param text the String to put into the text field
     */
    public void setText(String text) {
        this.text.setText(text);
    }

    /**
     * Sets the date in the date selection drop-down list.
     *
     * @param date the date selection
     */
    public void setDate(String date) {
        this.date.setSelectedIndex(calendar.getColumnIndex(date));
    }

    /**
     * Sets the time in the time selection drop-down list.
     *
     * @param time the time selection
     */
    public void setTime(String time) {
        this.time.setSelectedIndex(Util.indexOf(time, times));
    }

    /**
     * Sets the priority in the priority selection drop-down list.
     *
     * @param priority the priority selection
     */
    public void setPriority(int priority) {
        this.priority.setSelectedIndex(priority);
    }

    /**
     * Returns a String array containing the times available for a Task to be assigned. Index zero
     * contains "11:59 PM" and the rest of the indexes are a range from "8:00 AM" to "8:00 PM" on
     * half-hour intervals.
     *
     * @return the String array of times
     */
    public String[] getTimes() {
        List<String> timeList = new ArrayList<>();

        timeList.add("11:59 PM");
        int startHour = 8;
        int stopHour = 8;
        for (int hour = startHour; hour <= 12; hour++) {
            for (int minute = 0; minute <= 30; minute += 30) {
                if (hour == 12) {
                    timeList.add(hour + ":" + new DecimalFormat("00").format(minute) + " PM");
                    continue;
                }
                timeList.add(hour + ":" + new DecimalFormat("00").format(minute) + " AM");
            }
        }
        for (int hour = 1; hour <= stopHour; hour++) {
            for (int minute = 0; minute <= 30; minute += 30) {
                timeList.add(hour + ":" + new DecimalFormat("00").format(minute) + " PM");
                if (hour == stopHour) {
                    break;
                }
            }
        }
        return Arrays.copyOf(timeList.toArray(), timeList.size(), String[].class);
    }

    private JComboBox<String> createJComboBox(String[] items, int defaultIndex) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setSelectedIndex(defaultIndex);
        cb.setBackground(Color.WHITE);

        return cb;
    }

    private JButton createSubmitButton() {
        JButton b = new JButton("Create Task");
        b.setBorder(BorderFactory.createRaisedBevelBorder());
        b.setFocusable(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("Arial", Font.PLAIN, 16));
        b.setMargin(new Insets(10, 10, 10, 10));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                submitTask();
            }
        });
        return b;
    }

    private void resetJComboBox(JComboBox<String> cb, String[] items, int defaultIndex) {
        cb.removeAllItems();
        for (String s : items) {
            cb.addItem(s);
        }
        cb.setSelectedIndex(defaultIndex);
    }
}
