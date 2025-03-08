package com.example.GreetingApp.controller;

import com.example.GreetingApp.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }


    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String message)
    {

        emailService.sendEmail(to, subject, message);
        return ResponseEntity.ok("Email sent successfully!");
    }


    @PostMapping("/send-with-attachment")
    public ResponseEntity<String> sendEmailWithAttachment(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String message,
            @RequestParam("file") MultipartFile file,
            @RequestParam String replyTo) {

        File tempFile = null;
        try {

            tempFile = new File(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(file.getBytes());
            }


            emailService.sendEmailWithAttachment(to, subject, message, tempFile.getAbsolutePath(), replyTo);

            return ResponseEntity.ok("Email with attachment sent successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to process the file: " + e.getMessage());
        } finally {

            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}