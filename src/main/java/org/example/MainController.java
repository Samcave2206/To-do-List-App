package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.TaskStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Comparator;
import org.example.TaskManager;



import org.example.service.AIService;

public class MainController {


    @FXML private TableView<Task> table;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<Integer> priorityCombo;
    @FXML private DatePicker dueDatePicker;

    private TaskManager taskManager;
    private String currentUser;
    Dotenv dotenv = Dotenv.load();

    String apiKey = dotenv.get("API_KEY");
    String appPassword = dotenv.get("APP_PASSWORD");
    String fromEmail = dotenv.get("FROM_EMAIL");
    // AI service
    private AIService ai = new AIService(apiKey);

    public void setUser(String email) {
        this.currentUser = email;
        System.out.println("Logged in user: " + email);
    }

    public void init(TaskStorage storage) {
        this.taskManager = new TaskManager(storage);
        priorityCombo.getItems().addAll(1, 2, 3, 4, 5);
        refreshTable();
    }

    private void refreshTable() {
        table.getItems().setAll(taskManager.loadTasks());
    }

    @FXML
    private void handleAdd() {
        String title = titleField.getText();
        String description = descriptionField.getText();
        Integer priority = priorityCombo.getValue();
        LocalDate dueDate = dueDatePicker.getValue();

        if (title == null || title.isEmpty()) {
            showAlert("Title cannot be empty!");
            return;
        }

        Task newTask = new Task(title, description, priority, dueDate);

        taskManager.addTask(newTask);
        refreshTable();
        clearFields();
    }

    @FXML
    private void handleEdit() {
        Task selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a task to edit.");
            return;
        }

        String title = titleField.getText();
        String description = descriptionField.getText();
        Integer priority = priorityCombo.getValue();
        LocalDate dueDate = dueDatePicker.getValue();

        if (title == null || title.isEmpty()) {
            showAlert("Title cannot be empty!");
            return;
        }

        Task updatedTask = new Task(title, description, priority, dueDate);
        updatedTask.setId(selected.getId());

        taskManager.updateTask(updatedTask);
        refreshTable();
        clearFields();
    }

    @FXML
    private void handleAISuggest() {
        String title = titleField.getText();

        if (title == null || title.isEmpty()) {
            showAlert("Enter a task title before asking AI!");
            return;
        }

        // Call AI service
        String aiText = ai.generateDescription(title);

        descriptionField.setText(aiText);
    }

    @FXML
    private void handleMarkAsCompleted() {
        Task selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a task to mark as completed.");
            return;
        }

        selected.setPriority(0);
        taskManager.updateTask(selected);
        refreshTable();

        showAlert("Task marked as completed!");
    }

    @FXML
    private void handleSortByDeadline() {
        List<Task> tasks = taskManager.loadTasks();
        tasks.sort(Comparator.comparing(Task::getDueDate));
        table.getItems().setAll(tasks);
    }

    @FXML
    private void handleSortByPriority() {
        List<Task> tasks = taskManager.loadTasks();
        tasks.sort(Comparator.comparing(Task::getPriority).reversed());
        table.getItems().setAll(tasks);
    }

    @FXML
    private void handleLogout() {
        SceneManager.switchToLogin();
    }

    @FXML
    private void handleDelete() {
        Task selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a task to delete.");
            return;
        }

        taskManager.deleteTask(selected.getId().toString());
        refreshTable();
        clearFields();
    }

    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
        priorityCombo.setValue(null);
        dueDatePicker.setValue(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.show();
    }
}
