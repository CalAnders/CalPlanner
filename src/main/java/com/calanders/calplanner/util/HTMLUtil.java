package com.calanders.calplanner.util;

import com.calanders.calplanner.data.Task;

import java.util.UUID;

/**
 * A utility class for Task and Calendar operations.
 */
public class HTMLUtil {
    private static final String LOW_COLOR_HTML = "rgb(0, 192, 0)";
    private static final String MEDIUM_COLOR_HTML = "rgb(255, 176, 0)";
    private static final String HIGH_COLOR_HTML = "rgb(255, 64, 64)";
    private static final String UUID_HTML = "<p style=\"font-size: 0px;\">";

    /**
     * Generates the HTML code for a given Task object as a String type. The HTML code can be used to
     * create a display in a Component.
     *
     * @param task the Task to be converted to HTML
     * @return the HTML String
     */
    public static String getTaskHTML(Task task) {
        String color = switch (task.getPriority()) {
            case Task.PRIORITY_LOW -> LOW_COLOR_HTML;
            case Task.PRIORITY_HIGH -> HIGH_COLOR_HTML;
            default -> MEDIUM_COLOR_HTML;
        };

        String html =
                "<html>"
                    + "<p style=\"font-size: 12px; color: " + color + "; font-weight: bold;\">" + task.getText() + "</p>"
                    + "<p style=\"font-size: 10px;\">" + task.getTime() + "</p>"
                    + UUID_HTML + task.getUUID() + "</p>"
                + "</html>";

        return html;
    }

    /**
     * Locates the Task UUID from the HTML String.
     *
     * @param html the HTML String to find the UUID from
     * @return the UUID located from the HTML String
     */
    public static UUID getUUIDFromHTML(String html) {
        int uuidStart = html.indexOf(UUID_HTML) + UUID_HTML.length();
        int uuidEnd = uuidStart + 36;
        return UUID.fromString(html.substring(uuidStart, uuidEnd));
    }
}
