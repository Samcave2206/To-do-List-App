package org.example;

import org.example.TaskStorage;

import java.util.List;

public class TaskManager {

    private final TaskStorage storage;

    public TaskManager(TaskStorage storage) {
        this.storage = storage;
    }

    // Load luôn từ MongoDB
    public List<Task> loadTasks() {
        return storage.loadTasks();
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
