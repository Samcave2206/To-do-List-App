package org.example.service;

import org.bson.Document;
import org.example.storage.MongoUserStorage;

import java.time.LocalDateTime;
import java.util.Objects;

public class AuthService {

    private final MongoUserStorage userStorage;
    private final OTPService otpService;

    public AuthService(MongoUserStorage storage, OTPService otpService) {
        this.userStorage = storage;
        this.otpService = otpService;
    }

    private String hashPassword(String raw) {
        return Integer.toHexString(Objects.hash(raw));
    }

    // REGISTER
    public enum RegisterResult {
        SUCCESS,
        EMAIL_EXISTS,
        FAILED_TO_SEND_OTP
    }

    public RegisterResult register(String email, String password) {

        Document existing = userStorage.getUser(email);
        if (existing != null) return RegisterResult.EMAIL_EXISTS;

        // Create user first (emailVerified=false)
        boolean created = userStorage.createUser(email, hashPassword(password));
        if (!created) return RegisterResult.EMAIL_EXISTS;

        // Generate OTP
        String otp = otpService.generateOTP();

        // Send email
        if (!otpService.sendOTP(email, otp))
            return RegisterResult.FAILED_TO_SEND_OTP;
        //Nếu còn lỗi thì có nên tạo thêm collection?

        userStorage.saveOTP(email, otp, LocalDateTime.now().plusMinutes(5));

        return RegisterResult.SUCCESS;
    }

    // VERIFY
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

        String correct = user.getString("otp");
        LocalDateTime expire = userStorage.getOTPExpire(email);

        if (correct == null || expire == null)
            return VerifyResult.WRONG_OTP;

        if (LocalDateTime.now().isAfter(expire))
            return VerifyResult.EXPIRED;

        if (!correct.equals(inputOtp))
            return VerifyResult.WRONG_OTP;

        userStorage.setEmailVerified(email);
        userStorage.clearOTP(email);
        System.out.println(">>> stored otp = " + correct);
        System.out.println(">>> input otp = " + inputOtp);


        return VerifyResult.SUCCESS;
    }
 //Chưa test được logic nhưng mà UI có vấn đề, phải kiểm tra lại, hoặc là xoá luôn nó đi để tránh tạo thêm việc
    // RESEND OTP
    public enum ResendResult {
        SUCCESS,
        FAILED,
        NO_USER,
        ALREADY_VERIFIED
    }

    public ResendResult resendOTP(String email) {

        Document user = userStorage.getUser(email);
        if (user == null) return ResendResult.NO_USER;

        boolean verified = user.getBoolean("emailVerified", false);
        if (verified) return ResendResult.ALREADY_VERIFIED;

        String otp = otpService.generateOTP();
        if (!otpService.sendOTP(email, otp)) return ResendResult.FAILED;

        userStorage.saveOTP(email, otp, LocalDateTime.now().plusMinutes(5));
        return ResendResult.SUCCESS;
    }

    // LOGIN: mới test thử trước khi tạo AI generator, sau đó chưa thử, phải thử lại.
    public enum LoginResult {
        SUCCESS,
        WRONG_PASSWORD,
        USER_NOT_FOUND,
        EMAIL_NOT_VERIFIED
    }

    public LoginResult login(String email, String password) {

        Document user = userStorage.getUser(email);
        if (user == null) return LoginResult.USER_NOT_FOUND;

        if (!userStorage.isEmailVerified(email))
            return LoginResult.EMAIL_NOT_VERIFIED;

        String hashed = hashPassword(password);
        return userStorage.checkPassword(email, hashed)
                ? LoginResult.SUCCESS
                : LoginResult.WRONG_PASSWORD;
    }
}
