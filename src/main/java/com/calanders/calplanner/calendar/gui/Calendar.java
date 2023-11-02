package com.calanders.calplanner.calendar.gui;

import com.calanders.calplanner.calendar.data.FileManager;
import com.calanders.calplanner.calendar.gui.table.CalendarModel;
import com.calanders.calplanner.calendar.task.Task;
import com.calanders.calplanner.calendar.task.TaskUtil;

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

    public Calendar() {
        instance = this;
        taskCreator = new TaskCreator(instance);
        calendar = new JPanel();
        calendarModel = new CalendarModel(new String[7], rowCount);
        calendarTable = new JTable(calendarModel);
//        calendarTable = new JTable(new DefaultTableModel(new String[7], 20));
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
        calendarTable.setFillsViewportHeight(true);
        calendarTable.setShowHorizontalLines(false);
        calendarTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        calendarTable.setCellSelectionEnabled(true);
//        calendarTable.setDefaultRenderer(Task.class, new TaskRenderer());
        calendarTable.setRowHeight(100);
        calendarTable.setFont(new Font("Arial", Font.PLAIN, 16));
        calendarTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = calendarTable.rowAtPoint(e.getPoint());
                int col = calendarTable.columnAtPoint(e.getPoint());
                if (row >= 0 && col >= 0) {
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

    public void addTask(Task task) {
        tasks.add(task);
        fileManager.serialize(tasks);
        renderTasks();
    }

    public void editTask(int row, int col) {
        Task task = getTaskFromUUID(TaskUtil.getUUIDFromHTML(calendarTable.getValueAt(row, col).toString()));
        taskCreator.display(task);
        tasks.remove(task);
        calendarTable.setValueAt(null, row, col);
        fileManager.serialize(tasks);
    }

    public void deleteTask(int row, int col) {
        Task task = getTaskFromUUID(TaskUtil.getUUIDFromHTML(calendarTable.getValueAt(row, col).toString()));
        tasks.remove(task);
        calendarTable.setValueAt(null, row, col);
        fileManager.serialize(tasks);
    }

    private Task getTaskFromUUID(UUID uuid) {
        for (Task t : tasks) {
            if (t.getUUID().equals(uuid)) {
                return t;
            }
        }
        return null;
    }

    public void renderTasks() {
        calendarModel.setRowCount(0);
        calendarModel.setRowCount(rowCount);
        for (int col = 0; col < getWeekDates().length; col++) {
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getDate().equals(getWeekDates()[col])) {
                    for (int row = 0; row < calendarTable.getRowCount(); row++) {
                        if (getColumn(tasks.get(i).getDate()) != null) {
                            if (calendarTable.getValueAt(row, getColumn(tasks.get(i).getDate()).getModelIndex()) == null) {
                                calendarTable.setValueAt(TaskUtil.getTaskHTML(tasks.get(i)), row, getColumn(tasks.get(i).getDate()).getModelIndex());
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

    public void setCalendarWeek(int offset) {
        weekOffset = offset;
        String[] weekDates = getWeekDates();

        for (int col = 0; col < 7; col++) {
            calendarTable.getTableHeader().getColumnModel().getColumn(col).setHeaderValue(weekDates[col]);
        }
        calendarTable.getTableHeader().repaint();

        renderTasks();
    }

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

    public String[] getWeekDates() {
        java.util.List<LocalDate> weekDates = getWeek();
        String[] dates = new String[7];

        for (int col = 0; col < 7; col++) {
            dates[col] = weekDates.get(col).getDayOfWeek() + " " + (weekDates.get(col).getMonthValue()) + "-" + weekDates.get(col).getDayOfMonth();
        }

        return dates;
    }

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

    public int getSelectedRow() {
        return calendarTable.getSelectedRow();
    }

    public int getSelectedColumn() {
        return calendarTable.getSelectedColumn();
    }

    private JButton createNewTaskButton(String text) {
        JButton b = createControlButton(text);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                taskCreator.display();
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
                    editTask(getSelectedRow(), getSelectedColumn());
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
                    deleteTask(calendarTable.getSelectedRow(), calendarTable.getSelectedColumn());
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
