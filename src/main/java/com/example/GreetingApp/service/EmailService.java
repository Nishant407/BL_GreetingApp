package com.example.GreetingApp.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Load sender email from application.properties
    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Send email without attachment
    public void sendEmail(String to, String subject, String message) {
        sendEmailWithReplyTo(to, subject, message, null, null);
    }

    // Send email with attachment
    public void sendEmailWithAttachment(String to, String subject, String message, String attachmentPath, String replyTo) {
        sendEmailWithReplyTo(to, subject, message, attachmentPath, replyTo);
    }

    // Common method to send an email (with or without attachment and replyTo)
    private void sendEmailWithReplyTo(String to, String subject, String message, String attachmentPath, String replyTo) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, true);
            helper.setFrom(senderEmail);

            // Only set replyTo if it's not null or empty
            if (replyTo != null && !replyTo.isEmpty()) {
                helper.setReplyTo(replyTo);
            }

            // If an attachment path is provided, add the attachment
            if (attachmentPath != null && !attachmentPath.isEmpty()) {
                File attachment = new File(attachmentPath);
                if (!attachment.exists()) {
                    throw new IllegalArgumentException("Attachment file not found: " + attachmentPath);
                }
                helper.addAttachment(attachment.getName(), attachment);
            }

            // Send the email
            mailSender.send(mimeMessage);
            System.out.println("✅ Email sent successfully!");
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}
