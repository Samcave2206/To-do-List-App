package org.example.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.example.SceneManager;
import org.example.service.AuthService;

public class OTPController {

    @FXML private Label titleLabel;
    @FXML private TextField otpField;
    @FXML private Label countdownLabel;
    @FXML private Label errorLabel;
    @FXML private Button resendBtn;

    private String email;
    private AuthService auth;

    private int secondsLeft = 300; // 5 minutes
    private Timeline timeline;

    public void setEmail(String email) {
        this.email = email;
        titleLabel.setText("Enter OTP sent to: " + email);
        startCountdown();
    }

    public void initialize() {
        auth = SceneManager.getAuthService();
    }

    private void startCountdown() {
        if (timeline != null) timeline.stop();

        resendBtn.setDisable(true);
        countdownLabel.setText("Time left: 300s");

        secondsLeft = 300;

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            countdownLabel.setText("Time left: " + secondsLeft + "s");

            if (secondsLeft <= 0) {
                countdownLabel.setText("OTP expired. Request new OTP.");
                resendBtn.setDisable(false);
                timeline.stop();
            }
        }));

        timeline.setCycleCount(300);
        timeline.play();
    }

    @FXML
    public void handleVerify() {
        String otp = otpField.getText().trim();

        var result = auth.verifyOTP(email, otp);

        switch (result) {

            case SUCCESS:
                SceneManager.switchToMainApp(email);
                break;

            case WRONG_OTP:
                errorLabel.setText("Wrong OTP!");
                break;

            case EXPIRED:
                errorLabel.setText("OTP expired!");
                break;

            case NO_SUCH_USER:
                errorLabel.setText("No pending OTP found. Please register again.");
                break;

            default:
                errorLabel.setText("Verification error.");
                break;
        }
    }

    @FXML
    public void handleResend() {

        resendBtn.setDisable(true);

        var result = auth.resendOTP(email);

        switch (result) {

            case SUCCESS:
                errorLabel.setText("OTP sent!");
                startCountdown();
                break;

            case ALREADY_VERIFIED:
                errorLabel.setText("Email already verified.");
                break;

            case NO_USER:
                errorLabel.setText("No pending user found.");
                break;

            default:
                errorLabel.setText("Failed to resend OTP.");
                resendBtn.setDisable(false);
                break;
        }
    }
}
