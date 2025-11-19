package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.SceneManager;
import org.example.service.AuthService;

public class RegisterController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private AuthService auth;

    public void initialize() {
        this.auth = SceneManager.getAuthService();
    }

    @FXML
    public void handleRegister() {

        String email = emailField.getText();
        String pw = passwordField.getText();

        var result = auth.register(email, pw);

        switch (result) {
            case SUCCESS:
                errorLabel.setText("");
                SceneManager.switchToOTP(email);
                break;

            case EMAIL_EXISTS:
                errorLabel.setText("Email already exists.");
                break;

            case FAILED_TO_SEND_OTP:
                errorLabel.setText("Error sending OTP. Try again.");
                break;
        }
    }

    @FXML
    public void goToLogin() {
        SceneManager.switchToLogin();
    }
}
