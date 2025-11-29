package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import org.example.SceneManager;
import org.example.model.User;

public class ProfileController {

    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Hyperlink logoutButton;
    @FXML private Button backButton;

    private String currentUser;

    public void init(String email) {
        this.currentUser = email;

        User user = SceneManager.getAuthService().getUser(email);
        if (user != null) {
            nameLabel.setText(user.getName());
            emailLabel.setText(user.getEmail());
        }

        if (logoutButton != null) {
            logoutButton.setOnAction(e -> SceneManager.switchToLogin());
        }
        if (backButton != null) {
            backButton.setOnAction(e -> SceneManager.switchToMain(email));
        }
    }
}
