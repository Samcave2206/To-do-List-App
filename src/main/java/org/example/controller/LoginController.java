package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.SceneManager;
import org.example.service.AuthService;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private AuthService auth;

    public void initialize() {
        this.auth = SceneManager.getAuthService();
    }

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String pw = passwordField.getText();

        var result = auth.login(email, pw);

        switch (result) {

            case SUCCESS:
                errorLabel.setText("");
                SceneManager.switchToMainApp(email);
                break;

            case EMAIL_NOT_VERIFIED:
                errorLabel.setText("Email not verified. Please enter OTP.");
                SceneManager.switchToOTP(email);
                break;

            case WRONG_PASSWORD:
                errorLabel.setText("Wrong password.");
                break;

            case USER_NOT_FOUND:
                errorLabel.setText("Account does not exist.");
                break;
        }
    }

    @FXML
    public void goToRegister() {
        SceneManager.switchToRegister();
    }
}
