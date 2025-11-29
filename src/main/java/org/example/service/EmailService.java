package org.example.service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    private final String fromEmail;
    private final String appPassword;

    public EmailService(String fromEmail, String appPassword) {
        this.fromEmail = fromEmail;
        this.appPassword = appPassword;
    }

    private Session getSession() {
        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });
    }


    public void sendEmail(String to, String subject, String content) throws Exception {

        // Create message
        Message message = new MimeMessage(getSession());
        message.setFrom(new InternetAddress(fromEmail, "ToDo App"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        message.setText(content);
        System.out.println("Sending email...");

        Transport.send(message);
    }
}
