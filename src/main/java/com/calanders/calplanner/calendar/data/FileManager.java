package com.calanders.calplanner.calendar.data;

import com.calanders.calplanner.calendar.task.Task;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A class that operates as the file management system for Task serialization. This class is used
 * to create a directory for Tasks to be saved using JSON formatting. FileManager is used to save
 * and retrieve JSON data that holds Task objects.
 */
public class FileManager {
    private final File directory;
    private final File tasks;
    private FileWriter taskWriter;
    private FileReader taskReader;

    /**
     * Constructs a new FileManager. This constructor will attempt to create the directory of the
     * saved Tasks as well as the necessary readers and writers.
     */
    public FileManager() {
        directory = new File(System.getenv("APPDATA"), "CalPlanner");
        tasks = new File(directory + File.separator + "tasks.json");

        if (directory.mkdirs()) {
            directory.setReadable(true);
        }

        try {
            taskWriter = new FileWriter(tasks, true);
            taskWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts the List of Task objects into JSON data saved in the system. This data is put into
     * a JSON array which will hold the instances of Task objects inside.
     *
     * @param taskList the List of Tasks to be serialized
     */
    public void serialize(List<Task> taskList) {
        try {
            taskWriter = new FileWriter(tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray taskArray = getJsonArray(taskList);
        try {
            taskWriter.write(taskArray.toJSONString());
            taskWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            taskWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONArray getJsonArray(List<Task> taskList) {
        JSONArray taskArray = new JSONArray();
        for (Task task : taskList) {
            JSONObject taskDetails = new JSONObject();
            taskDetails.put("text", task.getText());
            taskDetails.put("date", task.getDate());
            taskDetails.put("time", task.getTime());
            taskDetails.put("priority", task.getPriority());
            taskDetails.put("uuid", task.getUUID().toString());
            JSONObject taskObject = new JSONObject();
            taskObject.put("task", taskDetails);
            taskArray.add(taskObject);
        }
        return taskArray;
    }

    /**
     * Retrieves the List of Tasks from the JSON data saved in the system.
     *
     * @return the List of Tasks retrieved from JSON
     */
    public List<Task> deserialize() {
        try {
            taskReader = new FileReader(tasks);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<Task> taskList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            Object object = parser.parse(taskReader);
            JSONArray taskArray = (JSONArray) object;
            for (Object obj : taskArray) {
                taskList.add(parseTaskObject((JSONObject) obj));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            taskReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return taskList;
    }

    private Task parseTaskObject(JSONObject object) {
        Task task;
        JSONObject taskObject = (JSONObject) object.get("task");
        String text = taskObject.get("text").toString();
        String date = taskObject.get("date").toString();
        String time = taskObject.get("time").toString();
        int priority = Integer.valueOf(taskObject.get("priority").toString());
        UUID uuid = UUID.fromString(taskObject.get("uuid").toString());

        task = new Task(text, date, time, priority);
        task.setUUID(uuid);

        return task;
    }
}
