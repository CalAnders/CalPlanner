package com.calanders.calplanner.calendar.gui;

import com.calanders.calplanner.calendar.task.Task;
import com.calanders.calplanner.calendar.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class that serves to provide a creation and editing interface for Tasks added to a Calendar. The
 * TaskCreator can generate a new Task based on user input and selections which will be added to the
 * Calendar upon submittal. Also, a TaskCreator can edit an existing Task if supplied with a Task object.
 */
public class TaskCreator {
    private final JFrame frame;
    private final Calendar calendar;
    private final JPanel panel;
    private final JPanel formPanel;
    private String[] dates;
    private final String[] times = createTimes();
    private final String[] priorities = { "Priority: Low", "Priority: Medium", "Priority: High" };
    private JTextField taskTitle;
    private JComboBox date;
    private JComboBox time = createJComboBox(times, 0);
    private JComboBox priority = createJComboBox(priorities, 1);
    private JButton submit = createSubmitButton("Create Task");

    /**
     * Constructs a new TaskCreator with a Calendar to create and add Tasks to.
     *
     * @param calendar the Calendar to create Tasks for
     */
    public TaskCreator(Calendar calendar) {
        this.calendar = calendar;
        frame = new JFrame("New Task");
        panel = new JPanel();
        formPanel = new JPanel();
        dates = calendar.getWeekDates();
        taskTitle = new JTextField();
        date = createJComboBox(dates, calendar.getCurrentDayOfWeek().getValue() - 1);

        init();
    }

    private void init() {
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(calendar);
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                calendar.addTask(getTask());
            }
        });

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
        taskTitle.setToolTipText("Enter task name");
        formPanel.add(taskTitle, formConstraints);
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

        frame.add(panel);
        frame.pack();
        frame.setVisible(false);
    }

    /**
     * Displays the TaskCreator to create a new Task. This will display the necessary options to create
     * a new Task as a graphical user interface.
     */
    public void create() {
        dates = calendar.getWeekDates();
        taskTitle.setText(null);
        resetJComboBox(date, dates, calendar.getCurrentDayOfWeek().getValue() - 1);
        resetJComboBox(time, times, 0);
        resetJComboBox(priority, priorities, Task.PRIORITY_MEDIUM);
        submit.setText("Create Task");
        frame.setTitle("New Task");
        frame.setVisible(true);
    }

    /**
     * Displays the TaskCreator with options populated based on the Task argument.
     *
     * @param task the Task to edit
     */
    public void edit(Task task) {
        dates = calendar.getWeekDates();
        taskTitle.setText(task.getText());
        resetJComboBox(date, dates, calendar.getSelectedColumn());
        resetJComboBox(time, times, Util.indexOf(times, task.getTime()));
        resetJComboBox(priority, priorities, task.getPriority());
        submit.setText("Update Task");
        frame.setTitle("Edit Task");
        frame.setVisible(true);
    }

    private Task getTask() {
        return new Task(taskTitle.getText(), date.getSelectedItem().toString(), time.getSelectedItem().toString(), priority.getSelectedIndex());
    }

    private JComboBox createJComboBox(String[] items, int defaultIndex) {
        JComboBox cb = new JComboBox(items);
        cb.setSelectedIndex(defaultIndex);
        cb.setBackground(Color.WHITE);

        return cb;
    }

    private JComboBox resetJComboBox(JComboBox cb, String[] items, int defaultIndex) {
        cb.removeAllItems();
        for (String s : items) {
            cb.addItem(s);
        }
        cb.setSelectedIndex(defaultIndex);
        return cb;
    }

    private JButton createSubmitButton(String text) {
        JButton b = new JButton(text);
        b.setBorder(BorderFactory.createRaisedBevelBorder());
        b.setFocusable(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("Arial", Font.PLAIN, 16));
        b.setMargin(new Insets(10, 10, 10, 10));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                calendar.addTask(getTask());
                frame.dispose();
            }
        });
        return b;
    }

    private String[] createTimes() {
        List<String> timeList = new ArrayList<>();
        String[] times;

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
        times = Arrays.copyOf(timeList.toArray(), timeList.size(), String[].class);

        return times;
    }
}
