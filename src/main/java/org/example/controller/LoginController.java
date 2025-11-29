package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.SceneManager;
import org.example.model.User;
import org.example.session.Session;
import org.example.service.AuthService;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private AuthService auth;

    public void initialize() {
        this.auth = SceneManager.getAuthService();
        errorLabel.setText("");
    }

    @FXML
    public void handleLogin() {
        String email = emailField.getText().trim().toLowerCase();
        String pw = passwordField.getText().trim();

        if (email.isEmpty() || pw.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        var result = auth.login(email, pw);

        switch (result) {

            case SUCCESS:
                errorLabel.setText("");
                User loggedIn = auth.getUser(email);
                Session.currentUser = loggedIn;
                javafx.application.Platform.runLater(() -> SceneManager.switchToMain(email));
                break;

            case EMAIL_NOT_VERIFIED:
                errorLabel.setText("Email not verified. Enter OTP.");
                javafx.application.Platform.runLater(() -> SceneManager.switchToOTP(email));
                break;

            case WRONG_PASSWORD:
                errorLabel.setText("Wrong password.");
                break;

            case USER_NOT_FOUND:
                errorLabel.setText("Account does not exist.");
                break;

            default:
                errorLabel.setText("Login failed.");
                break;
        }
    }

    @FXML
    public void goToRegister() {
        SceneManager.switchToRegister();
    }
}
