package org.example.service;

import org.bson.Document;
import org.example.model.User;
import org.example.storage.MongoUserStorage;

//import com.mongodb.client.model.Filters;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

public class AuthService {

    private final MongoUserStorage userStorage;
    private final OTPService otpService;

    public AuthService(MongoUserStorage storage, OTPService otpService) {
        this.userStorage = storage;
        this.otpService = otpService;
    }

    // -------------------------------------------------------------------------
    // PASSWORD HASHING (SHA-256)
    // -------------------------------------------------------------------------
    private String hashPassword(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(raw.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : encoded) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // fallback
            return Integer.toHexString(raw.hashCode());
        }
    }

    // -------------------------------------------------------------------------
    // REGISTER
    // -------------------------------------------------------------------------
    public enum RegisterResult {
        SUCCESS,
        EMAIL_EXISTS,
        FAILED_TO_SEND_OTP
    }

    public RegisterResult register(String name, String email, String password) {

        Document existing = userStorage.getUser(email);
        if (existing != null)
            return RegisterResult.EMAIL_EXISTS;

        boolean created = userStorage.createUser(
                name,
                email,
                hashPassword(password)
        );

        if (!created)
            return RegisterResult.EMAIL_EXISTS;

        // generate OTP
        String otp = otpService.generateOTP();

        // attempt email sending
        if (!otpService.sendOTP(email, otp))
            return RegisterResult.FAILED_TO_SEND_OTP;

        // save OTP with expiration
        userStorage.saveOTP(email, otp, LocalDateTime.now().plusMinutes(1));

        return RegisterResult.SUCCESS;
    }

    // -------------------------------------------------------------------------
    // VERIFY OTP
    // -------------------------------------------------------------------------
    public enum VerifyResult {
        SUCCESS,
        WRONG_OTP,
        EXPIRED,
        NO_SUCH_USER
    }

    public VerifyResult verifyOTP(String email, String inputOtp) {

        Document user = userStorage.getUser(email);
        if (user == null)
            return VerifyResult.NO_SUCH_USER;

        String storedOtp = user.getString("otp");
        LocalDateTime expire = userStorage.getOTPExpire(email);

        if (storedOtp == null || expire == null)
            return VerifyResult.WRONG_OTP;

        if (LocalDateTime.now().isAfter(expire))
            return VerifyResult.EXPIRED;

        if (!storedOtp.equals(inputOtp))
            return VerifyResult.WRONG_OTP;

        // success
        userStorage.setEmailVerified(email);
        userStorage.clearOTP(email);

        return VerifyResult.SUCCESS;
    }

    // -------------------------------------------------------------------------
    // RESEND OTP
    // -------------------------------------------------------------------------
    public enum ResendResult {
        SUCCESS,
        FAILED,
        NO_USER,
        ALREADY_VERIFIED
    }

    public ResendResult resendOTP(String email) {

        Document user = userStorage.getUser(email);
        if (user == null)
            return ResendResult.NO_USER;

        boolean verified = user.getBoolean("emailVerified", false);
        if (verified)
            return ResendResult.ALREADY_VERIFIED;

        String newOTP = otpService.generateOTP();

        if (!otpService.sendOTP(email, newOTP))
            return ResendResult.FAILED;

        userStorage.saveOTP(email, newOTP, LocalDateTime.now().plusMinutes(5));

        return ResendResult.SUCCESS;
    }

    // -------------------------------------------------------------------------
    // LOGIN
    // -------------------------------------------------------------------------
    public enum LoginResult {
        SUCCESS,
        WRONG_PASSWORD,
        USER_NOT_FOUND,
        EMAIL_NOT_VERIFIED
    }

    public LoginResult login(String email, String password) {

        // normalize email to match storage (emails are stored lower-case)
        String normalizedEmail = email == null ? null : email.toLowerCase();

        Document user = userStorage.getUser(normalizedEmail);
        if (user == null)
            return LoginResult.USER_NOT_FOUND;

        boolean verified = userStorage.isEmailVerified(normalizedEmail);
        if (!verified)
            return LoginResult.EMAIL_NOT_VERIFIED;

        String hashed = hashPassword(password);

        boolean ok = userStorage.checkPassword(normalizedEmail, hashed);
        return ok ? LoginResult.SUCCESS : LoginResult.WRONG_PASSWORD;
    }

    // ========================================================================
    //  GET USER MODEL (Optional)
    // ========================================================================

    public User getUser(String email) {
        Document doc = userStorage.getUser(email.toLowerCase());

        if (doc == null) return null;

        User user = new User();
        user.setId(doc.getObjectId("_id").toString());
        user.setName(doc.getString("name"));
        user.setEmail(doc.getString("email"));
        user.setPassword(doc.getString("password"));
        user.setVerified(doc.getBoolean("verified", false));
        user.setOtp(doc.getString("otp"));

        return user;
    }

}
