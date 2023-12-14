package com.calanders.calplanner.gui;

import com.calanders.calplanner.data.Settings;
import com.calanders.calplanner.data.Task;
import com.calanders.calplanner.data.resources.FileManager;
import com.calanders.calplanner.data.resources.Resources;
import com.calanders.calplanner.gui.table.CalendarModel;
import com.calanders.calplanner.util.HTMLUtil;
import com.calanders.calplanner.util.Util;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.*;
import java.util.logging.Logger;

/**
 * A class that represents a seven-day calendar in which tasks may be added and modified. The Calendar is
 * displayed with seven columns to signify days of the week. Tasks can be added to the Calendar and are
 * placed in their corresponding date.
 *
 * @author CalAnders
 * @version 1.0.6
 */
public class Calendar {
    public static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static final String TITLE = "CalPlanner";
    public static final String VERSION = "1.0.6";
    private final JFrame frame;
    private final JPanel panel;
    private final JPanel menuPanel;
    private final JButton createTaskButton;
    private final JButton editTaskButton;
    private final JButton deleteTaskButton;
    private final JButton homeButton;
    private final JButton overviewButton;
    private final JButton settingsButton;
    private final CalendarModel calendarModel;
    private final JTable calendarTable;
    private final JPanel calendarPanel;
    private final JButton last;
    private final JButton next;
    private final TaskMenu taskMenu;
    private final SettingsMenu settingsMenu;
    private final FileManager fileManager;
    private final ArrayList<Task> tasks;
    private Settings settings;
    private static final Color MENU_COLOR = new Color(216, 216, 216);
    private int weekOffset = 0;
    private int rowCount = 26;

    /**
     * Constructs a new Calendar object with all required components.
     */
    public Calendar() {
        frame = new JFrame(TITLE + " " + VERSION);
        panel = new JPanel();
        menuPanel = new JPanel();
        createTaskButton = createNewTaskButton();
        editTaskButton = createEditTaskButton();
        deleteTaskButton = createDeleteTaskButton();
        homeButton = createControlButton("Home", Resources.HOME_ICON);
        overviewButton = createControlButton("Overview", Resources.OVERVIEW_ICON);
        settingsButton = createControlButton("Settings", Resources.SETTINGS_ICON);
        calendarModel = new CalendarModel(new String[7], rowCount);
        calendarTable = new JTable(calendarModel);
        calendarPanel = new JPanel();
        last = createNavButton("←");
        next = createNavButton("→");
        taskMenu = new TaskMenu(this);
        settingsMenu = new SettingsMenu(this);
        fileManager = new FileManager();
        tasks = fileManager.getTasks();
        settings = fileManager.getSettings();

        init();
        initMenu();
        initCalendar();
        frame.add(panel);
    }

    private void init() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        frame.setIconImage(Resources.CALENDAR_ICON);
        frame.setMinimumSize(new Dimension(960, 540));
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        panel.setLayout(new BorderLayout());
    }

    private void initMenu() {
        menuPanel.setLayout(new BorderLayout());
        menuPanel.setBackground(MENU_COLOR);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel nav = new JPanel();
        nav.setBackground(MENU_COLOR);
        nav.add(createTaskButton);
        nav.add(Box.createRigidArea(new Dimension(5, 0)));
        nav.add(editTaskButton);
        nav.add(Box.createRigidArea(new Dimension(5, 0)));
        nav.add(deleteTaskButton);
        nav.add(Box.createRigidArea(new Dimension(5, 0)));
        homeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                setCalendarWeek(0);
            }
        });
        nav.add(homeButton);
        nav.add(Box.createRigidArea(new Dimension(5, 0)));
        nav.add(overviewButton);
        nav.add(Box.createRigidArea(new Dimension(5, 0)));
        settingsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                settingsMenu.display();
            }
        });
        nav.add(settingsButton);
        menuPanel.add(nav, BorderLayout.LINE_END);
        panel.add(menuPanel, BorderLayout.PAGE_START);
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
        calendarTable.setRowHeight(120);
        calendarTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                refreshMenu();
                if (isTaskSelected()) {
                    if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                        taskMenu.displayEditor(Objects.requireNonNull(getTask(
                                calendarTable.getSelectedRow(),
                                calendarTable.getSelectedColumn())));
                    }
                } else {
                    if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                        taskMenu.displayCreator();
                        taskMenu.setDate(getWeekDates()[calendarTable.getSelectedColumn()]);
                    }
                }
            }
        });
        calendarTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                refreshMenu();
                if (isTaskSelected()) {
                    if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                        deleteTask(getTask(calendarTable.getSelectedRow(),
                                calendarTable.getSelectedColumn()));
                    }
                }
            }
        });

        calendarPanel.setLayout(new BorderLayout());
        calendarPanel.setBackground(MENU_COLOR);
        last.addActionListener(e -> setCalendarWeek(--weekOffset));
        calendarPanel.add(last, BorderLayout.LINE_START);
        calendarPanel.add(new JScrollPane(calendarTable), BorderLayout.CENTER);
        next.addActionListener(e -> setCalendarWeek(++weekOffset));
        calendarPanel.add(next, BorderLayout.LINE_END);

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                last.setFont(new Font("Arial", Font.BOLD, e.getComponent().getWidth() / 40));
                next.setFont(new Font("Arial", Font.BOLD, e.getComponent().getWidth() / 40));
            }
        });
        panel.add(calendarPanel);
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
            update();
        }
    }

    /**
     * Modifies the Task contained in the Calendar that matches the specified Task. This method looks
     * at the UUID of the Task argument and locates the Task with the exact UUID in the Calendar. If
     * the Calendar does not contain a Task with this UUID, it will not be added to the Calendar.
     *
     * @param task the Task to edit
     */
    public void editTask(Task task) {
        if (task != null) {
            int row = getTaskRow(task);
            int col = getTaskColumn(task);
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getUUID().toString().equals(task.getUUID().toString())) {
                    tasks.set(i, task);
                    calendarTable.setValueAt(task, row, col);
                    update();
                    break;
                }
            }
        }
    }

    /**
     * Deletes the Task at the selected row and column if it contains a valid Task. This method will
     * remove the Task from storage and the Calendar display.
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
            update();
        }
    }

    private void update() {
        fileManager.saveTasks(tasks);
        renderTasks();
        editTaskButton.setEnabled(false);
        deleteTaskButton.setEnabled(false);
    }

    /**
     * Renders the Tasks from storage. This will render all Tasks for all dates, so only one call is
     * required for all Tasks until one of them is modified.
     */
    public void renderTasks() {
        calendarModel.setRowCount(0);
        rowCount = Util.clamp(getAppropriateRowCount(weekOffset), 10, Integer.MAX_VALUE) + 1;
        calendarModel.setRowCount(rowCount);
        for (int col = 0; col < getWeekDates().length; col++) {
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getDate().equals(getWeekDates()[col])) {
                    for (int row = 0; row < calendarTable.getRowCount(); row++) {
                        if (getColumn(tasks.get(i).getDate()) != null) {
                            if (calendarTable.getValueAt(row, col) == null) {
                                calendarTable.setValueAt(HTMLUtil.getTaskHTML(tasks.get(i)), row, col);
                                break;
                            }
                        }
                    }
                } else {
                    if (i < rowCount) {
                        calendarTable.setValueAt(null, i, col);
                    }
                }
            }
        }
    }

    private int getAppropriateRowCount(int weekOffset) {
        HashMap<String, Integer> dateCounts = new HashMap<>();
        for (Task task : tasks) {
            if (Util.indexOf(task.getDate(), getWeekDates(weekOffset)) != -1) {
                if (dateCounts.containsKey(task.getDate())) {
                    dateCounts.put(task.getDate(), dateCounts.get(task.getDate()) + 1);
                } else {
                    dateCounts.put(task.getDate(), 1);
                }
            }
        }
        int count = 0;
        for (Map.Entry<String, Integer> entry : dateCounts.entrySet()) {
            if (entry.getValue() >= count) {
                count = entry.getValue();
            }
        }
        return count;
    }

    /**
     * Replaces the Settings of this Calendar instance with the specified Settings.
     *
     * @param settings the Settings to modify
     */
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    /**
     * Retrieves the Settings associated with this Calendar instance.
     *
     * @return the Settings of the Calendar
     */
    public Settings getSettings() {
        return settings;
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
     * Returns whether the specified row and column of the Calendar contains a valid Task or not.
     *
     * @param row the row
     * @param col the column
     * @return true if Task at (row, col) is valid, false otherwise.
     */
    public boolean isValidTask(int row, int col) {
        return getTask(row, col) != null;
    }

    /**
     * Returns whether the selected row and column of the Calendar contains a valid Task or not.
     *
     * @return true if the selected cell in Calendar contains a valid Task, false otherwise.
     */
    public boolean isTaskSelected() {
        if (calendarTable.getSelectedRow() == -1 || calendarTable.getSelectedColumn() == -1) {
            return false;
        } else {
            return isValidTask(calendarTable.getSelectedRow(), calendarTable.getSelectedColumn());
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
     * Retrieves the display names of the days of the offset specified week. This will be in the
     * format of "DAY month-day-year" For example, a possible date is "SUNDAY 8-20-2023".
     *
     * @param offset the offset in weeks from current week
     * @return an array of Strings containing the names of the week days
     */
    public String[] getWeekDates(int offset) {
        LocalDate localDate = LocalDate.now().plusWeeks(offset);
        List<LocalDate> weekDates = new ArrayList<>();
        for (int i = 1; i < localDate.getDayOfWeek().getValue(); i++) {
            weekDates.add(localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.of(i))));
        }
        for (int i = localDate.getDayOfWeek().getValue(); i <= 7; i++) {
            weekDates.add(localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.of(i))));
        }

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
     * Retrieves the display names of the days of the set week. This will be in the
     * format of "DAY month-day-year" For example, a possible date is "SUNDAY 8-20-2023".
     *
     * @return an array of Strings containing the names of the week days
     */
    public String[] getWeekDates() {
        return getWeekDates(weekOffset);
    }

    /**
     * Retrieves the current day of the week according to the user's system.
     *
     * @return the day of the week as a DayOfWeek object
     */
    public DayOfWeek getCurrentDayOfWeek() {
        return LocalDate.now().getDayOfWeek();
    }

    /**
     * Retrieves the index of the Calendar's selected column.
     *
     * @return the column index
     */
    public int getSelectedColumn() {
        return calendarTable.getSelectedColumn();
    }

    /**
     * Retrieves the index of the Calendar's selected row.
     *
     * @return the row index
     */
    public int getSelectedRow() {
        return calendarTable.getSelectedRow();
    }

    private TableColumn getColumn(Object title) {
        for (int col = 0; col < 7; col++) {
            if (calendarTable.getColumnModel().getColumn(col).getHeaderValue().equals(title)) {
                return calendarTable.getColumnModel().getColumn(col);
            }
        }
        return null;
    }

    /* Component methods */

    /**
     * Calculates the top-left Point for a Component to be drawn with respect to the mouse
     * location. The Component's specified width and height are used to keep the container on
     * the same screen as the mouse.
     *
     * @param width the width of the Component
     * @param height the height of the Component
     * @return the top-left location of the Component
     */
    public static Point getPointForComponent(double width, double height) {
        double taskbarHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight()
                - GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight();
        PointerInfo pi = MouseInfo.getPointerInfo();
        Point mouseLocation = pi.getLocation();
        Rectangle rect = pi.getDevice().getDefaultConfiguration().getBounds();

        if (mouseLocation.getX() + width > rect.getX() + rect.getWidth()) {
            mouseLocation.setLocation((
                    rect.getX() + rect.getWidth()) - width,
                    mouseLocation.getY());
        }
        if (mouseLocation.getY() + height > rect.getY() + rect.getHeight() - taskbarHeight) {
            mouseLocation.setLocation(
                    mouseLocation.getX(),
                    ((rect.getY() + rect.getHeight()) - height - taskbarHeight));
        }

        return mouseLocation;
    }

    private void refreshMenu() {
        if (isTaskSelected()) {
            editTaskButton.setEnabled(true);
            deleteTaskButton.setEnabled(true);
        } else {
            editTaskButton.setEnabled(false);
            deleteTaskButton.setEnabled(false);
        }
    }

    private JButton createNewTaskButton() {
        JButton b = createControlButton("New");
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                taskMenu.displayCreator();
            }
        });
        return b;
    }

    private JButton createEditTaskButton() {
        JButton b = createControlButton("Edit");
        b.setEnabled(false);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (b.isEnabled()) {
                    taskMenu.displayEditor(Objects.requireNonNull(getTask(
                            calendarTable.getSelectedRow(),
                            calendarTable.getSelectedColumn())));
                }
            }
        });
        return b;
    }

    private JButton createDeleteTaskButton() {
        JButton b = createControlButton("Delete");
        b.setEnabled(false);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (b.isEnabled()) {
                    deleteTask(getTask(
                            calendarTable.getSelectedRow(),
                            calendarTable.getSelectedColumn()));
                }
            }
        });
        return b;
    }

    private JButton createControlButton(String text) {
        JButton b = new JButton(text);
        b.setToolTipText(text + " Task");
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setFocusable(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setContentAreaFilled(false);
        b.setFont(new Font("Arial", Font.BOLD, 20));
        return b;
    }

    private JButton createControlButton(String toolTip, Icon icon) {
        JButton b = new JButton(icon);
        b.setToolTipText(toolTip);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setFocusable(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setContentAreaFilled(false);
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

    /**
     * The main method of the application. This method creates a new Calendar window.
     *
     * @param args the command line arguments array
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calendar::new);
    }
}
