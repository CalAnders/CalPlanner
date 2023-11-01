package com.calanders.calplanner.calendar.task;

public class Task {
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_MEDIUM = 1;
    public static final int PRIORITY_HIGH = 2;
    private String text;
    private String date;
    private String time;
    private int priority;

    public Task(String text, String date, String time, int priority) {
        this.text = text;
        this.date = date;
        this.time = time;
        this.priority = priority;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "Task{" + "text='" + text + '\'' + ", date='" + date + '\'' + ", time='" + time + '\'' + ", priority=" + priority + '}';
    }
}
