package com.calanders.calplanner.data.resources;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * A class used to hold references to all the resources used in CalPlanner.
 */
public class Resources {
    /**
     * The Image that is displayed in the system environment.
     */
    public static final Image CALENDAR_ICON = new ImageIcon(
            Objects.requireNonNull(Resources.class.getResource("/icons/calendar.png"))).getImage();

    /**
     * The ImageIcon that is used for the settings button.
     */
    public static final ImageIcon SETTINGS_ICON = new ImageIcon(
            Objects.requireNonNull(Resources.class.getResource("/icons/settings.png")));

    /**
     * The ImageIcon that is used for the overview button.
     */
    public static final ImageIcon OVERVIEW_ICON = new ImageIcon(
            Objects.requireNonNull(Resources.class.getResource("/icons/overview.png")));

    /**
     * The ImageIcon that is used for the home button.
     */
    public static final ImageIcon HOME_ICON = new ImageIcon(
            Objects.requireNonNull(Resources.class.getResource("/icons/home.png")));
}
