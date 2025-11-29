package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.SceneManager;
import org.example.service.AuthService;

public class RegisterController {

    @FXML private TextField nameField;

    @FXML private TextField emailField;

    @FXML private PasswordField passwordField;

    @FXML private PasswordField confirmPasswordField;

    private final AuthService auth = SceneManager.getAuthService();

    @FXML
    private void handleRegister() {

        String name = nameField.getText();
        String email = emailField.getText();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        if (!pass.equals(confirm)) {
            showAlert("Passwords do not match!");
            return;
        }

        var created = auth.register(name, email, pass);

        switch (created) {
            case SUCCESS:
                showAlert("");
                SceneManager.switchToOTP(email);
                break;

            case EMAIL_EXISTS:
                showAlert("Email already exists.");
                SceneManager.switchToLogin();
                break;

            case FAILED_TO_SEND_OTP:
                showAlert("Error sending OTP. Try again.");
                break;
        }
    }

    @FXML
    private void goToLogin() {
        SceneManager.switchToLogin();
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).show();
    }
}
