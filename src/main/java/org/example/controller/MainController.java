package org.example.controller;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import org.example.SceneManager;
import org.example.TaskManager;
import org.example.TaskStorage;
import org.example.model.Task;
import org.example.session.Session;
import org.example.service.AIService;
import org.example.model.User;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class MainController {

    // Containers in MainView.fxml
    @FXML private VBox activeTaskContainer;
    @FXML private VBox completedTaskContainer;

    @FXML private Button addTaskBtn;
    @FXML private Button filterDeadlineBtn;
    @FXML private Button filterPriorityBtn;
    @FXML private Label greetingLabel;

    private TaskManager taskManager;
    private String currentUser;

    Dotenv dotenv = Dotenv.load();
    String apiKey = dotenv.get("API_KEY");
    private AIService ai = new AIService(apiKey);

    // ------------------------------------------------------------
    // INITIALIZATION
    // ------------------------------------------------------------

    public void init(TaskStorage storage, String email) {
        this.taskManager = new TaskManager(storage);
        this.currentUser = email;

        System.out.println("Logged in as: " + email);

        if (greetingLabel != null) {
            greetingLabel.setText("Hello " + resolveDisplayName(email));
        }

        loadTasksToUI();
        setupListeners();
    }

    private void setupListeners() {
        addTaskBtn.setOnAction(e -> SceneManager.switchToAddTask(currentUser));
        filterDeadlineBtn.setOnAction(e -> loadTasksSortedByDeadline());
        filterPriorityBtn.setOnAction(e -> loadTasksSortedByPriority());
    }

    // ------------------------------------------------------------
    // LOAD TASKS INTO UI
    // ------------------------------------------------------------

    private void loadTasksToUI() {
        activeTaskContainer.getChildren().clear();
        completedTaskContainer.getChildren().clear();

        List<Task> tasks = taskManager.loadTasks(currentUser);

        for (Task task : tasks) {
            Parent card = loadTaskCard(task);

            if (isCompleted(task)) {
                completedTaskContainer.getChildren().add(card);
            } else {
                activeTaskContainer.getChildren().add(card);
            }
        }
    }

    private Parent loadTaskCard(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Components/TaskCard.fxml"));
            Parent card = loader.load();

            TaskCardController controller = loader.getController();
            controller.setTask(task,
                    this::handleMarkCompleted,
                    this::handleViewTaskDetails,
                    this::handleDeleteTask);

            return card;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ------------------------------------------------------------
    // ACTION HANDLERS
    // ------------------------------------------------------------

    private void handleMarkCompleted(Task task) {
        task.setStatus("Completed");
        taskManager.updateTask(task);
        loadTasksToUI();
    }

    private void handleViewTaskDetails(Task task) {
        SceneManager.switchToTaskDetails(task, currentUser);
    }

    private void handleDeleteTask(Task task) {
        taskManager.deleteTask(task.getId().toString());
        loadTasksToUI();
    }
    @FXML
    private void handleProfile() {
        SceneManager.switchToProfile(currentUser);
    }

    // ------------------------------------------------------------
    // FILTERING
    // ------------------------------------------------------------

    private void loadTasksSortedByDeadline() {
        List<Task> tasks = taskManager.loadTasks(currentUser);
        tasks.sort(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())));
        rebuildUIFromList(tasks);
    }

    private void loadTasksSortedByPriority() {
        List<Task> tasks = taskManager.loadTasks(currentUser);
        tasks.sort(Comparator.comparing(Task::getPriority).reversed());
        rebuildUIFromList(tasks);
    }

    private void rebuildUIFromList(List<Task> tasks) {
        activeTaskContainer.getChildren().clear();
        completedTaskContainer.getChildren().clear();

        for (Task task : tasks) {
            Parent card = loadTaskCard(task);
            if (isCompleted(task))
                completedTaskContainer.getChildren().add(card);
            else
                activeTaskContainer.getChildren().add(card);
        }
    }

    // ------------------------------------------------------------
    // LOGOUT
    // ------------------------------------------------------------

    @FXML
    private void handleLogout() {
        Session.currentUser = null;
        SceneManager.switchToLogin();
    }

    private String formatUserName(String email) {
        if (email == null || email.isBlank()) {
            return "";
        }
        String name = email;
        int atIdx = email.indexOf("@");
        if (atIdx > 0) {
            name = email.substring(0, atIdx);
        }
        if (name.isEmpty()) {
            return email;
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private String resolveDisplayName(String email) {
        User user = Session.currentUser;
        if (user != null && user.getName() != null && !user.getName().isBlank()) {
            return user.getName();
        }
        return formatUserName(email);
    }

    private boolean isCompleted(Task task) {
        return "Completed".equalsIgnoreCase(task.getStatus());
    }
}
