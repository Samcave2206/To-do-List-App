package org.example;

//import org.example.TaskStorage;
import org.example.model.Task;

import java.util.List;

public class TaskManager {

    private final TaskStorage storage;

    public TaskManager(TaskStorage storage) {
        this.storage = storage;
    }

    // Load from MongoDB
    public List<Task> loadTasks(String userEmail) {
        return storage.loadTasks(userEmail);
    }

    public void addTask(Task task) {
        storage.addTask(task);
    }

    public void updateTask(Task task) {
        storage.updateTask(task);
    }

    public void deleteTask(String id) {
        storage.deleteTask(id);
    }
}
