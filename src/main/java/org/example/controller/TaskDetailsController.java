package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.example.TaskManager;
import org.example.model.Task;
import org.example.SceneManager;

import java.time.format.DateTimeFormatter;

public class TaskDetailsController {

    @FXML private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private Label priorityLabel;
    @FXML private Label statusLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button backButton;

    private Task task;
    private TaskManager taskManager;
    private String currentUser;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public void init(TaskManager manager, Task task, String email) {
        this.task = task;
        this.taskManager = manager;
        this.currentUser = email;

        titleLabel.setText(task.getTitle());
        descriptionArea.setText(task.getDescription());

        if (task.getDueDate() != null)
            dateLabel.setText("Due: " + task.getDueDate().format(formatter));
        else
            dateLabel.setText("No deadline");

        priorityLabel.setText(mapPriorityLabel(task.getPriority()));
        statusLabel.setText(task.getStatus());

        editButton.setOnAction(e -> SceneManager.switchToEditTask(task, email));
        deleteButton.setOnAction(e -> handleDelete());
        backButton.setOnAction(e -> SceneManager.switchToMain(email));
    }

    private void handleDelete() {
        taskManager.deleteTask(task.getId().toString());
        SceneManager.switchToMain(currentUser);
    }

    private String mapPriorityLabel(int priority) {
        return switch (priority) {
            case 3 -> "High";
            case 2 -> "Medium";
            case 1 -> "Low";
            default -> "Unknown";
        };
    }
}
