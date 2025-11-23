package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.example.controller.*;
import org.example.model.Task;
import org.example.service.AuthService;

public class SceneManager {

    private static Stage primaryStage;
    private static AuthService authService;

    // Storage layer (MongoDB)
    private static TaskStorage taskStorage;

    // Inject dependencies from Main.java
    public static void init(Stage stage, AuthService service, TaskStorage storage) {
        primaryStage = stage;
        authService = service;
        taskStorage = storage;
    }

    public static AuthService getAuthService() {
        return authService;
    }

    public static TaskStorage getTaskStorage() {
        return taskStorage;
    }

    // -------------------------------------------------------------------------
    // GENERAL SIMPLE SCREEN LOADING
    // -------------------------------------------------------------------------
    private static void loadSimple(String name) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/views/" + name));
            Parent root = loader.load();

            primaryStage.setScene(new Scene(root, 420, 750));
            primaryStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // LOGIN / REGISTER / OTP SCREENS
    // -------------------------------------------------------------------------

    public static void switchToLogin() {
        loadSimple("Login.fxml");
    }

    public static void switchToRegister() {
        loadSimple("Register.fxml");
    }

    /**
     * OTP screen receives the email to verify
     */
    public static void switchToOTP(String email) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/views/OTP.fxml"));
            Parent root = loader.load();

            OTPController controller = loader.getController();
            controller.setEmail(email);

            primaryStage.setScene(new Scene(root, 420, 750));
            primaryStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // MAIN HOME SCREEN (Task List)
    // -------------------------------------------------------------------------

    public static void switchToMain(String userEmail) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/views/MainView.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.init(taskStorage, userEmail);

            primaryStage.setScene(new Scene(root, 420, 750));
            primaryStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // PROFILE SCREEN
    // -------------------------------------------------------------------------

    public static void switchToProfile(String userEmail) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/views/Profile.fxml"));
            Parent root = loader.load();

            ProfileController controller = loader.getController();
            controller.init(userEmail);

            primaryStage.setScene(new Scene(root, 420, 750));
            primaryStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // ADD TASK SCREEN
    // -------------------------------------------------------------------------

    public static void switchToAddTask(String userEmail) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/views/AddTask.fxml"));
            Parent root = loader.load();

            AddTaskController controller = loader.getController();
            controller.init(new TaskManager(taskStorage), userEmail);

            primaryStage.setScene(new Scene(root, 420, 750));
            primaryStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // EDIT TASK SCREEN
    // -------------------------------------------------------------------------

    public static void switchToEditTask(Task task, String userEmail) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/views/EditTask.fxml"));
            Parent root = loader.load();

            EditTaskController controller = loader.getController();
            controller.init(new TaskManager(taskStorage), task, userEmail);

            primaryStage.setScene(new Scene(root, 420, 750));
            primaryStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // TASK DETAILS SCREEN
    // -------------------------------------------------------------------------

    public static void switchToTaskDetails(Task task, String userEmail) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/views/TaskDetails.fxml"));
            Parent root = loader.load();

            TaskDetailsController controller = loader.getController();
            controller.init(new TaskManager(taskStorage), task, userEmail);

            primaryStage.setScene(new Scene(root, 420, 750));
            primaryStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
