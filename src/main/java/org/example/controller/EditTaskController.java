package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import org.example.SceneManager;
import org.example.TaskManager;
import org.example.model.Task;

public class EditTaskController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ToggleGroup priorityToggleGroup;
    @FXML private ToggleButton highPriorityBtn;
    @FXML private ToggleButton mediumPriorityBtn;
    @FXML private ToggleButton lowPriorityBtn;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button closeButton;

    private TaskManager taskManager;
    private Task task;
    private String currentUser;

    public void init(TaskManager manager, Task task, String email) {
        this.taskManager = manager;
        this.task = task;
        this.currentUser = email;

        titleField.setText(task.getTitle());
        descriptionField.setText(task.getDescription());
        dueDatePicker.setValue(task.getDueDate());
        selectPriorityButton(task.getPriority());

        statusCombo.getItems().setAll("Pending", "Completed");
        statusCombo.setValue(formatStatusLabel(task.getStatus()));

        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> navigateBack());
        closeButton.setOnAction(e -> navigateBack());
    }

    private void handleSave() {
        task.setTitle(titleField.getText());
        task.setDescription(descriptionField.getText());
        task.setPriority(resolveSelectedPriority());
        task.setDueDate(dueDatePicker.getValue());
        task.setStatus(resolveStatusValue());

        taskManager.updateTask(task);
        SceneManager.switchToTaskDetails(task, currentUser);
    }

    private void navigateBack() {
        SceneManager.switchToTaskDetails(task, currentUser);
    }

    private void selectPriorityButton(int priority) {
        switch (priority) {
            case 3 -> priorityToggleGroup.selectToggle(highPriorityBtn);
            case 2 -> priorityToggleGroup.selectToggle(mediumPriorityBtn);
            default -> priorityToggleGroup.selectToggle(lowPriorityBtn);
        }
    }

    private int resolveSelectedPriority() {
        if (highPriorityBtn.isSelected()) return 3;
        if (mediumPriorityBtn.isSelected()) return 2;
        return 1;
    }

    private String resolveStatusValue() {
        String status = statusCombo.getValue();
        if (status == null || status.isBlank()) {
            return "Pending";
        }
        return status;
    }

    private String formatStatusLabel(String status) {
        if (status == null || status.isBlank()) {
            return "Pending";
        }
        String normalized = status.trim();
        if (normalized.equalsIgnoreCase("completed")) {
            return "Completed";
        }
        return "Pending";
    }
}
