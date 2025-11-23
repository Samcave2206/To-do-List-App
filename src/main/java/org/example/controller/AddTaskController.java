package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import org.example.SceneManager;
import org.example.TaskManager;
import org.example.model.Task;
import org.example.service.AIService;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.LocalDate;

public class AddTaskController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker dueDatePicker;
    @FXML private ToggleGroup priorityToggleGroup;
    @FXML private ToggleButton highPriorityBtn;
    @FXML private ToggleButton mediumPriorityBtn;
    @FXML private ToggleButton lowPriorityBtn;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button aiButton;
    @FXML private Button backButton;

    private TaskManager taskManager;
    private String currentUser;

    Dotenv dotenv = Dotenv.load();
    private AIService ai = new AIService(dotenv.get("API_KEY"));

    public void init(TaskManager manager, String email) {
        this.taskManager = manager;
        this.currentUser = email;

        priorityToggleGroup.selectToggle(mediumPriorityBtn);
        statusCombo.getItems().setAll("Pending", "Completed");
        statusCombo.setValue("Pending");

        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> SceneManager.switchToMain(email));
        if (backButton != null) {
            backButton.setOnAction(e -> SceneManager.switchToMain(email));
        }
        aiButton.setOnAction(e -> handleAISuggest());
    }

    private void handleSave() {
        String title = titleField.getText();
        String desc = descriptionField.getText();
        LocalDate dueDate = dueDatePicker.getValue();
        String status = statusCombo.getValue();

        if (title == null || title.isBlank()) {
            showAlert("Title cannot be empty");
            return;
        }

        Task task = new Task(currentUser, title, desc, dueDate, resolveSelectedPriority(), status);
        taskManager.addTask(task);

        SceneManager.switchToMain(currentUser);
    }

    private int resolveSelectedPriority() {
        if (highPriorityBtn.isSelected()) return 3;
        if (mediumPriorityBtn.isSelected()) return 2;
        return 1;
    }

    private void handleAISuggest() {
        String title = titleField.getText();
        if (title == null || title.isEmpty()) {
            showAlert("Enter a title first");
            return;
        }
        descriptionField.setText(ai.generateDescription(title));
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).show();
    }
}
