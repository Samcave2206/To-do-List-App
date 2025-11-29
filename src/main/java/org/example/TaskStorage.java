package org.example;

import java.util.List;

import org.example.model.Task;

public interface TaskStorage {
    List<Task> loadTasks(String userEmail);
    void addTask(Task task);
    void updateTask(Task task);
    void deleteTask(String id);  // use _id to delete
}
