package com.calanders.calplanner.calendar.gui;

import com.calanders.calplanner.calendar.data.FileManager;
import com.calanders.calplanner.calendar.gui.table.CalendarModel;
import com.calanders.calplanner.calendar.task.Task;
import com.calanders.calplanner.calendar.task.HTMLUtil;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A class that represents a seven-day calendar in which tasks may be added and modified. The Calendar is
 * displayed with seven columns to signify days of the week. Tasks can be added to the Calendar and are
 * placed in their corresponding date.
 *
 * @author CalAnders
 * @version 1.1
 */
public class Calendar extends JPanel {
    private final Calendar instance;
    private final TaskCreator taskCreator;
    private final JPanel calendar;
    private final CalendarModel calendarModel;
    private final JTable calendarTable;
    private final JPanel menu;
    private final JButton createTaskButton;
    private final JButton editTaskButton;
    private final JButton deleteTaskButton;
    private final JButton last;
    private final JButton next;
    private final FileManager fileManager;
    private final List<Task> tasks;
    private static final Color MENU_COLOR = new Color(216, 216, 216);
    private int weekOffset = 0;
    private int rowCount = 20;

    /**
     * Constructs a new Calendar object with all required components.
     */
    public Calendar() {
        instance = this;
        taskCreator = new TaskCreator(instance);
        calendar = new JPanel();
        calendarModel = new CalendarModel(new String[7], rowCount);
//        calendarTable = new JTable(new DefaultTableModel(new String[7], 20));
        calendarTable = new JTable(calendarModel);
        menu = new JPanel();
        createTaskButton = createNewTaskButton("New Task");
        editTaskButton = createEditTaskButton("Edit Task");
        deleteTaskButton = createDeleteTaskButton("Delete Task");
        last = createNavButton("<");
        next = createNavButton(">");
        fileManager = new FileManager();
        tasks = fileManager.deserialize();

        setLayout(new BorderLayout());
        initMenu();
        initCalendar();
    }

    private void initMenu() {
        menu.setBackground(MENU_COLOR);
        menu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menu.add(createTaskButton);
        menu.add(editTaskButton);
        menu.add(deleteTaskButton);
        add(menu, BorderLayout.PAGE_START);
    }

    private void initCalendar() {
        calendarTable.getTableHeader().setBorder(BorderFactory.createRaisedSoftBevelBorder());
        calendarTable.getTableHeader().setResizingAllowed(false);
        calendarTable.getTableHeader().setReorderingAllowed(false);
        calendarTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));
        calendarTable.getTableHeader().setBackground(MENU_COLOR);
        calendarTable.setSelectionBackground(calendarTable.getBackground());
        calendarTable.setFillsViewportHeight(true);
        calendarTable.setShowHorizontalLines(false);
        calendarTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        calendarTable.setCellSelectionEnabled(true);
        calendarTable.setRowHeight(100);
        calendarTable.setFont(new Font("Arial", Font.PLAIN, 16));
        calendarTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isTaskSelected(calendarTable.getSelectedRow(), calendarTable.getSelectedColumn())) {
                    editTaskButton.setEnabled(true);
                    deleteTaskButton.setEnabled(true);
                } else {
                    editTaskButton.setEnabled(false);
                    deleteTaskButton.setEnabled(false);
                }
            }
        });

        calendar.setLayout(new BorderLayout());
        calendar.setBackground(MENU_COLOR);
        last.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setCalendarWeek(--weekOffset);
            }
        });
        calendar.add(last, BorderLayout.LINE_START);
        calendar.add(new JScrollPane(calendarTable), BorderLayout.CENTER);
        next.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setCalendarWeek(++weekOffset);
            }
        });
        calendar.add(next, BorderLayout.LINE_END);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                last.setFont(new Font("Arial", Font.BOLD, e.getComponent().getWidth() / 40));
                next.setFont(new Font("Arial", Font.BOLD, e.getComponent().getWidth() / 40));
            }
        });

        add(calendar);
        setCalendarWeek(weekOffset);
    }

    /**
     * Adds a new Task to the Calendar. The Task argument will be appropriately stored and displayed
     * in the Calendar. First, the Task is stored on user's system and is then rendered into the Calendar.
     *
     * @param task the Task to be added
     */
    public void addTask(Task task) {
        if (task != null) {
            tasks.add(task);
            fileManager.serialize(tasks);
            renderTasks();
        }
    }

    /**
     * Displays the task editing window with the Task information loaded from the row and column selected
     * by the user if the row and column if it contains a valid Task. After the Task is modified, it will
     * replace the Task at the selected row and column.
     *
     * @param task the Task to edit
     */
    public void editTask(Task task) {
        if (task != null) {
            int row = getTaskRow(task);
            int col = getTaskColumn(task);
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getUUID().equals(task.getUUID())) {
                    System.out.println("changed from " + tasks.get(i) + "   to   " + task);
                    tasks.set(i, task);
                }
            }
            calendarTable.setValueAt(task, row, col);
            fileManager.serialize(tasks);
            renderTasks();
        }
    }

    /**
     * Deletes the Task at the selected row and column if it contains a valid Task. This method will remove
     * the Task from storage and the Calendar display.
     *
     * @param task the Task to delete
     */
    public void deleteTask(Task task) {
        if (task != null) {
            int row = getTaskRow(task);
            int col = getTaskColumn(task);
            tasks.remove(task);
            calendarTable.setValueAt(null, row, col);
            calendarModel.moveRow(row + 1, calendarTable.getRowCount() - 1, row);
            fileManager.serialize(tasks);
            renderTasks();
        }
    }

    /**
     * Retrieves the row index of a specified Task.
     *
     * @param task the Task
     * @return the row index of the Task or -1 if not found
     */
    public int getTaskRow(Task task) {
        for (int col = 0; col < getWeekDates().length; col++) {
            for (int row = 0; row < calendarTable.getRowCount(); row++) {
                if (calendarTable.getValueAt(row, col) != null) {
                    UUID cellUUID = HTMLUtil.getUUIDFromHTML(calendarTable.getValueAt(row, col).toString());
                    if (task.getUUID().toString().equals(cellUUID.toString())) {
                        System.out.println(row + ", " + col);
                        return row;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Retrieves the column index of a specified Task.
     *
     * @param task the Task
     * @return the column index of the Task or -1 if not found
     */
    public int getTaskColumn(Task task) {
        for (int col = 0; col < getWeekDates().length; col++) {
            for (int row = 0; row < calendarTable.getRowCount(); row++) {
                if (calendarTable.getValueAt(row, col) != null) {
                    UUID cellUUID = HTMLUtil.getUUIDFromHTML(calendarTable.getValueAt(row, col).toString());
                    if (task.getUUID().toString().equals(cellUUID.toString())) {
                        System.out.println(row + ", " + col);
                        return col;
                    }
                }
            }
        }
        return -1;
    }

    private Task getTask(int row, int col) {
        if (calendarTable.getValueAt(row, col) != null) {
            UUID cellUUID = HTMLUtil.getUUIDFromHTML(calendarTable.getValueAt(row, col).toString());
            for (Task t : tasks) {
                if (t.getUUID().equals(cellUUID)) {
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * Returns whether the selected row and column of the Calendar contains a valid Task or not.
     *
     * @param row the selected row
     * @param col the selected column
     * @return true if Task at (row, col) is valid, false otherwise.
     */
    public boolean isTaskSelected(int row, int col) {
        return getTask(row, col) != null;
    }

    /**
     * Renders the Tasks from storage. This will render all Tasks for all dates, so only one call is
     * required for all Tasks until one of them is modified.
     */
    public void renderTasks() {
        calendarModel.setRowCount(0);
        calendarModel.setRowCount(rowCount);
        for (int col = 0; col < getWeekDates().length; col++) {
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getDate().equals(getWeekDates()[col])) {
                    for (int row = 0; row < calendarTable.getRowCount(); row++) {
                        if (getColumn(tasks.get(i).getDate()) != null) {
                            if (calendarTable.getValueAt(row, getColumn(tasks.get(i).getDate()).getModelIndex()) == null) {
                                calendarTable.setValueAt(HTMLUtil.getTaskHTML(tasks.get(i)), row, getColumn(tasks.get(i).getDate()).getModelIndex());
                                break;
                            }
                        }
                    }
                } else {
                    calendarTable.setValueAt(null, i, col);
                }
            }
        }
    }

    /**
     * Sets the Calendar's week display to match an offset (in weeks) from the current week. For example,
     * calling setCalendarWeek(1) will display the next week. Also, setCalendarWeek(-1) will display the
     * last week.
     *
     * @param offset the offset in weeks from current week
     */
    public void setCalendarWeek(int offset) {
        weekOffset = offset;
        String[] weekDates = getWeekDates();
        for (int col = 0; col < 7; col++) {
            calendarTable.getTableHeader().getColumnModel().getColumn(col).setHeaderValue(weekDates[col]);
        }
        calendarTable.getTableHeader().repaint();
        renderTasks();
    }

    /**
     * Retrieves a List of LocalDate objects that refer to the Calendar's set week.
     *
     * @return the List of LocalDates for the Calendar's set week.
     */
    public List<LocalDate> getWeek() {
        LocalDate localDate = LocalDate.now().plus(weekOffset, ChronoUnit.WEEKS);
        List<LocalDate> weekDates = new ArrayList<>();
        for (int i = 1; i < localDate.getDayOfWeek().getValue(); i++) {
            weekDates.add(localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.of(i))));
        }
        for (int i = localDate.getDayOfWeek().getValue(); i <= 7; i++) {
            weekDates.add(localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.of(i))));
        }
        return weekDates;
    }

    /**
     * Retrieves the display names of the days of the set week. This will be in the format of "DAY month-day-year"
     * For example, a possible date is "SUNDAY 8-20-2023".
     *
     * @return an array of Strings containing the names of the week days
     */
    public String[] getWeekDates() {
        java.util.List<LocalDate> weekDates = getWeek();
        String[] dates = new String[7];
        for (int col = 0; col < 7; col++) {
            dates[col] = weekDates.get(col).getDayOfWeek() + " "
                    + weekDates.get(col).getMonthValue() + "-"
                    + weekDates.get(col).getDayOfMonth() + "-"
                    + weekDates.get(col).getYear();
        }
        return dates;
    }

    /**
     * Retrieves the current day of the week according to the user's system.
     *
     * @return the day of the week as a DayOfWeek object
     */
    public DayOfWeek getCurrentDayOfWeek() {
        return LocalDate.now().getDayOfWeek();
    }

    private TableColumn getColumn(Object title) {
        for (int col = 0; col < 7; col++) {
            if (calendarTable.getColumnModel().getColumn(col).getHeaderValue().equals(title)) {
                return calendarTable.getColumnModel().getColumn(col);
            }
        }
        return null;
    }

    /**
     * Retrieves the selected row index of the Calendar
     *
     * @return the selected row index of the Calendar
     */
    public int getSelectedRow() {
        return calendarTable.getSelectedRow();
    }

    /**
     * Retrieves the selected column index of the Calendar
     *
     * @return the selected column index of the Calendar
     */
    public int getSelectedColumn() {
        return calendarTable.getSelectedColumn();
    }

    private JButton createNewTaskButton(String text) {
        JButton b = createControlButton(text);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                taskCreator.create();
            }
        });
        return b;
    }

    private JButton createEditTaskButton(String text) {
        JButton b = createControlButton(text);
        b.setEnabled(false);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (b.isEnabled()) {
                    taskCreator.edit(getTask(calendarTable.getSelectedRow(), calendarTable.getSelectedColumn()));
                }
            }
        });
        return b;
    }

    private JButton createDeleteTaskButton(String text) {
        JButton b = createControlButton(text);
        b.setEnabled(false);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (b.isEnabled()) {
                    deleteTask(getTask(calendarTable.getSelectedRow(), calendarTable.getSelectedColumn()));
                }
            }
        });
        return b;
    }

    private JButton createControlButton(String text) {
        JButton b = new JButton(text);
        b.setBorder(BorderFactory.createRaisedBevelBorder());
        b.setFocusable(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("Arial", Font.BOLD, 16));
        b.setMargin(new Insets(10, 10, 10, 10));
        return b;
    }

    private JButton createNavButton(String text) {
        JButton b = new JButton(text);
        b.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        b.setFocusable(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("Arial", Font.BOLD, 40));
        b.setContentAreaFilled(false);
        return b;
    }
}
