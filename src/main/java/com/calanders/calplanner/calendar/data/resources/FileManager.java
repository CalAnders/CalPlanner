package com.calanders.calplanner.calendar.data.resources;

import com.calanders.calplanner.calendar.data.Settings;
import com.calanders.calplanner.calendar.data.Task;
import com.calanders.calplanner.calendar.gui.Calendar;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * A class that operates as the file management system for Calendar. Utilizing the Serializable interface,
 * the Tasks and Settings are saved to the system in the .ser file format.
 */
public class FileManager {
    private final File directory;
    private final File tasks;
    private final File settings;

    /**
     * Construct a new FileManager. This constructor will attempt to create the directories for the
     * Calendar application. If successful, the files will be marked as readable and are ready to be
     * written to and read from.
     */
    public FileManager() {
        directory = new File(System.getenv("APPDATA"), "CalPlanner");
        tasks = new File(directory + File.separator + "tasks.ser");
        settings = new File(directory + File.separator + "settings.ser");

        if (directory.mkdirs()) {
            directory.setReadable(true);
        }
    }

    /**
     * Save the ArrayList of Tasks into the system.
     *
     * @param tasks the ArrayList of Tasks to be serialized
     */
    public void serializeTasks(ArrayList<Task> tasks) {
        serialize(this.tasks, tasks);
    }

    /**
     * Retrieve the ArrayList of Tasks from the system.
     *
     * @return the ArrayList of Tasks
     */
    public ArrayList<Task> deserializeTasks() {
        return (ArrayList<Task>) deserialize(tasks);
    }

    /**
     * Save Settings into the system.
     *
     * @param settings the ArrayList of Settings to be serialized
     */
    public void serializeSettings(Settings settings) {
        serialize(this.settings, settings);
    }

    /**
     * Retrieve Settings from the system.
     *
     * @return the Settings
     */
    public Settings deserializeSettings() {
        return (Settings) deserialize(settings);
    }

    private void serialize(File file, Object object) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            fos.close();
        } catch (IOException e) {
            Calendar.LOGGER.log(Level.INFO, "Error occurred while writing stream header");
        }
    }

    private Object deserialize(File file) {
        Object contents = new Object();
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            contents = ois.readObject();
        } catch (IOException e) {
            Calendar.LOGGER.log(Level.INFO, "Tasks file is empty");
        } catch (ClassNotFoundException e) {
            Calendar.LOGGER.log(Level.WARNING, "Class Task not found");
        }

        return contents;
    }
}
