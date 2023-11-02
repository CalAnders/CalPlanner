package com.calanders.calplanner.calendar.task;

import java.util.UUID;

public class TaskUtil {
    public static String getTaskHTML(Task task) {
        String color;
        switch (task.getPriority()) {
            case Task.PRIORITY_LOW:
                color = "rgb(0, 192, 0)";
                break;
            case Task.PRIORITY_HIGH:
                color = "rgb(255, 64, 64)";
                break;
            default:
                color = "rgb(255, 176, 0)";
        }

        String html =
                "<html>"
                    + "<p style=\"font-size: 12px; color: " + color + "; font-weight: bold;\">" + task.getText() + "</p>"
                    + "<p style=\"font-size: 10px;\">" + task.getTime() + "</p>"
                    + "<p style=\"font-size: 0px;\">" + task.getUUID() + "</p>"
                + "</html>";
        return html;
    }

    public static UUID getUUIDFromHTML(String html) {
        String tag = "<p style=\"font-size: 0px;\">"; // This needs to match UUID HTML in getTaskHTML() exactly!
        int uuidStart = html.indexOf(tag) + tag.length();
        int uuidEnd = uuidStart + 36;
        return UUID.fromString(html.substring(uuidStart, uuidEnd));
    }
}
