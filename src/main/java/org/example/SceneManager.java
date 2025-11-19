package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.service.AuthService;
import org.example.MainController;

public class SceneManager {

    private static Stage primaryStage;
    private static AuthService authService;


    public static void init(Stage stage, AuthService service) {
        primaryStage = stage;
        authService = service;
    }

    public static AuthService getAuthService() {
        return authService;
    }

    private static void loadSimple(String name) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/views/" + name));
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root, 420, 520));
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  SWITCH SCREENS

    public static void switchToLogin() {
        loadSimple("login.fxml");
    }

    public static void switchToRegister() {
        loadSimple("register.fxml");
    }

    public static void switchToOTP(String email) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/views/otp.fxml")
            );

            Parent root = loader.load();

            // Gửi email sang OTPController
            org.example.controller.OTPController controller = loader.getController();
            controller.setEmail(email);

            primaryStage.setScene(new Scene(root, 420, 520));
            primaryStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  MAIN APPLICATION SCREEN

    public static void switchToMainApp(String userEmail) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/MainView.fxml")
            );

            Parent root = loader.load();

            // truyền userEmail sang MainController
            MainController controller = loader.getController();
            controller.setUser(userEmail);

            primaryStage.setScene(new Scene(root, 960, 600));
            primaryStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
