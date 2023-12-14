package com.calanders.calplanner.data.resources;

import com.calanders.calplanner.data.Settings;
import com.calanders.calplanner.data.Task;
import com.calanders.calplanner.gui.Calendar;

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
     * Constructs a new FileManager. This constructor will attempt to create the directories for the
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
        if (!tasks.exists()) {
            initFile(tasks, new ArrayList<Task>());
        }
        if (!settings.exists()) {
            initFile(settings, new Settings());
        }
    }

    /**
     * Saves the ArrayList of Tasks into the system.
     *
     * @param tasks the ArrayList of Tasks to be serialized
     */
    public void saveTasks(ArrayList<Task> tasks) {
        serialize(this.tasks, tasks);
    }

    /**
     * Retrieves the ArrayList of Tasks from the system.
     *
     * @return the ArrayList of Tasks
     */
    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            tasks = (ArrayList<Task>) deserialize(this.tasks);
        } catch (ClassCastException e) {
            Calendar.LOGGER.log(Level.INFO, "File " + this.tasks.getName() + " is invalid");
        }
        return tasks;
    }

    /**
     * Saves Settings into the system.
     *
     * @param settings the Settings to be serialized
     */
    public void saveSettings(Settings settings) {
        serialize(this.settings, settings);
    }

    /**
     * Retrieves Settings from the system.
     *
     * @return the Settings
     */
    public Settings getSettings() {
        Settings settings = new Settings();
        try {
            settings = (Settings) deserialize(this.settings);
        } catch (ClassCastException e) {
            Calendar.LOGGER.log(Level.INFO, "File " + this.settings.getName() +  " is invalid");
        }
        return settings;
    }

    private void serialize(File file, Object object) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            fos.close();
        } catch (IOException e) {
            Calendar.LOGGER.log(Level.WARNING, "Error occurred while writing stream header");
        }
    }

    private Object deserialize(File file) {
        Object contents = new Object();
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            contents = ois.readObject();
            ois.close();
            fis.close();
        } catch (ClassNotFoundException e) {
            Calendar.LOGGER.log(Level.SEVERE, "Class not found");
        } catch (IOException e) {
            Calendar.LOGGER.log(Level.INFO, "File " + file.getName() + " is invalid");
        }

        return contents;
    }

    private void initFile(File file, Object object) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            Calendar.LOGGER.log(Level.WARNING, "Could not create file " + file.getName());
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            fos.close();
        } catch (IOException e) {
            Calendar.LOGGER.log(Level.WARNING, "Error occurred while writing stream header");
        }
    }
}
