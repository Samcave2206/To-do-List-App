package org.example.service;

import java.util.Random;

public class OTPService {

    private final EmailService emailService;
    private final Random random = new Random();

    public OTPService(EmailService emailService) {
        this.emailService = emailService;
    }

    // Tạo OTP 6 số, cẩn thận bị mongo nuốt số 0, phải kiểm tra lại sau
    public String generateOTP() {
        return String.format("%06d", random.nextInt(999999));
    }

    // Gửi OTP qua email: đã nhận, nhưng mà phải có mail thật, liên tục chỉnh sửa collection.
    public boolean sendOTP(String email, String otp) {
        try {


            String subject = "Your Verification Code";
            String content =
                    "Your OTP code is: " + otp +
                            "\nThis code will expire in 5 minutes.";

            emailService.sendEmail(email, subject, content);
            try {
                emailService.sendEmail(email,
                        "Your Verification Code",
                        content
                );
                System.out.println("EMAIL SENT");
            } catch(Exception e) {
                System.out.println("EMAIL SEND FAILED");
                e.printStackTrace();
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
