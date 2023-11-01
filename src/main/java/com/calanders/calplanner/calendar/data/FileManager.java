package com.calanders.calplanner.calendar.data;

import com.calanders.calplanner.calendar.task.Task;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private final File directory;
    private final File tasks;
    private FileWriter taskWriter;
    private FileReader taskReader;

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
            JSONObject taskObject = new JSONObject();
            taskObject.put("task", taskDetails);
            taskArray.add(taskObject);
        }
        return taskArray;
    }

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
        JSONObject task = (JSONObject) object.get("task");
        String text = task.get("text").toString();
        String date = task.get("date").toString();
        String time = task.get("time").toString();
        int priority = Integer.valueOf(task.get("priority").toString());

        return new Task(text, date, time, priority);
    }
}
