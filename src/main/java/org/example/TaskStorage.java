package org.example;

import org.example.Task;
import java.util.List;

public interface TaskStorage {
    List<Task> loadTasks();
    void addTask(Task task);
    void updateTask(Task task);
    void deleteTask(String id);  // dùng _id để xoá
}
