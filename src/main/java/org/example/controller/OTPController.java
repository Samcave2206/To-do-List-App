package org.example.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.example.SceneManager;
import org.example.model.User;
import org.example.session.Session;
import org.example.service.AuthService;

public class OTPController {

    @FXML private Label titleLabel;
    @FXML private TextField otpField;
    @FXML private Label countdownLabel;
    @FXML private Label errorLabel;
    @FXML private Button resendBtn;

    private String email;
    private AuthService auth;

    private int secondsLeft = 300; // 5 min
    private Timeline timeline;

    public void initialize() {
        auth = SceneManager.getAuthService();
        errorLabel.setText("");   // Clear old errors
    }

    /**
     * Injected from SceneManager
     */
    public void setEmail(String email) {
        this.email = email;
        titleLabel.setText(email);
        startCountdown();
    }

    // -------------------------------------------------------------------------
    // COUNTDOWN TIMER
    // -------------------------------------------------------------------------
    private void startCountdown() {
        if (timeline != null) timeline.stop();

        resendBtn.setDisable(true);
        secondsLeft = 60;

        countdownLabel.setText("Resend available in 60s");

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            countdownLabel.setText("Resend available in " + secondsLeft + "s");

            if (secondsLeft <= 0) {
                countdownLabel.setText("OTP expired. Please request a new OTP.");
                resendBtn.setDisable(false);
                timeline.stop();
            }
        }));

        timeline.setCycleCount(300);
        timeline.play();
    }

    // -------------------------------------------------------------------------
    // VERIFY OTP
    // -------------------------------------------------------------------------
    @FXML
    public void handleVerify() {
        String otp = otpField.getText().trim();

        if (otp.isEmpty()) {
            errorLabel.setText("Please enter OTP.");
            return;
        }

        var result = auth.verifyOTP(email, otp);

        switch (result) {

            case SUCCESS:
                errorLabel.setText("");
                timeline.stop();
                User verifiedUser = auth.getUser(email);
                Session.currentUser = verifiedUser;
                SceneManager.switchToMain(email);
                break;

            case WRONG_OTP:
                errorLabel.setText("Incorrect OTP!");
                break;

            case EXPIRED:
                errorLabel.setText("OTP expired. Request a new one.");
                resendBtn.setDisable(false);
                break;

            case NO_SUCH_USER:
                errorLabel.setText("No OTP found. Please register again.");
                break;

            default:
                errorLabel.setText("Verification error.");
                break;
        }
    }

    // -------------------------------------------------------------------------
    // RESEND OTP
    // -------------------------------------------------------------------------
    @FXML
    public void handleResend() {

        resendBtn.setDisable(true);

        var result = auth.resendOTP(email);

        switch (result) {

            case SUCCESS:
                errorLabel.setText("A new OTP has been sent!");
                startCountdown();
                break;

            case NO_USER:
                errorLabel.setText("User not found. Please register again.");
                break;

            case ALREADY_VERIFIED:
                errorLabel.setText("Email already verified.");
                break;

            default:
                errorLabel.setText("Failed to resend OTP.");
                resendBtn.setDisable(false);
                break;
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchToRegister();
    }
}
