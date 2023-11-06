package com.calanders.calplanner.calendar.task;

import java.util.UUID;

/**
 * A class that represents a Task of which a text, date, time, and priority level exist.
 */
public class Task {
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_MEDIUM = 1;
    public static final int PRIORITY_HIGH = 2;
    private String text;
    private String date;
    private String time;
    private int priority;
    private UUID uuid;

    /**
     * Constructs a new Task object with a text, date, time, and priority level.
     *
     * @param text the text of the Task
     * @param date the date of the Task
     * @param time the time of the Task
     * @param priority the priority level of the Task
     */
    public Task(String text, String date, String time, int priority) {
        this.text = text;
        this.date = date;
        this.time = time;
        this.priority = priority;
        uuid = UUID.randomUUID();
    }

    /**
     * Sets the text of the Task.
     *
     * @param text the text of the Task
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Retrieves the text of the Task.
     *
     * @return the text of the Task
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the date of the Task.
     *
     * @param date the date of the Task
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Retrieves the date of the Task.
     *
     * @return the date of the Task
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the time of the Task.
     *
     * @param time the time of the Task
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Retrieves the time of the Task.
     *
     * @return the time of the Task
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets the priority of the Task.
     *
     * @param priority the priority of the Task
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Retrieves the priority of the Task.
     *
     * @return the priority of the Task
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the UUID of the Task.
     *
     * @param uuid the UUID of the Task
     */
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Retrieves the uuid of the Task.
     *
     * @return the uuid of the Task
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Converts the Task into its String representation.
     *
     * @return the String representation of Task
     */
    @Override
    public String toString() {
        return "Task{" +
                "text='" + text + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", priority=" + priority +
                ", uuid=" + uuid +
                '}';
    }
}
