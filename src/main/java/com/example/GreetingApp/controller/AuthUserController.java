package com.example.GreetingApp.controller;

import com.example.GreetingApp.dto.LoginDTO;
import com.example.GreetingApp.dto.UserDto;
import com.example.GreetingApp.dto.LoginResponseDto;
import com.example.GreetingApp.service.AuthUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;



@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "Handles User Authentication")
@Validated
public class AuthUserController {

    private final AuthUserService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthUserController.class);

    public AuthUserController(AuthUserService authService) {
        this.authService = authService;
    }

    @Operation(summary = "User Login", description = "Validates user credentials and returns a JWT token.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@RequestBody @Valid LoginDTO loginDto) {
        logger.info("🔐 Login attempt: {}", loginDto.getEmail());

        LoginResponseDto response = authService.loginUser(loginDto);

        if (response == null || response.getToken() == null) { // Handle login failure
            logger.warn("❌ Login failed for user: {}", loginDto.getEmail());
            return ResponseEntity.status(401).body(new LoginResponseDto(null, "Invalid credentials"));
        }

        logger.info("✅ Login successful for user: {}", loginDto.getEmail());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Register a new user", description = "Saves user details and returns success message.")
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserDto userDto) {
        logger.info("🚀 Register attempt: {}", userDto.getEmail());

        String response = authService.registerUser(userDto);

        if (response.startsWith("User registered successfully")) {
            logger.info("✅ Registration successful for: {}", userDto.getEmail());
            return ResponseEntity.status(201).body(response);
        } else {
            logger.error("❌ Registration failed: {}", response);
            return ResponseEntity.status(400).body(response);
        }
    }

    @Operation(summary = "Test Authenticated Endpoint", description = "Tests if the provided JWT token is valid.")
    @GetMapping("/test")
    public ResponseEntity<String> testAuthenticatedEndpoint(@RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("⚠️ Missing or malformed Authorization token");

            return ResponseEntity.status(400).body("Authorization token is missing or malformed.");
        }

        String token = authHeader.substring(7);
        boolean isValid = authService.validateToken(token);

        if (isValid) {
            logger.info("✅ JWT token is valid.");
            return ResponseEntity.ok("JWT token is valid.");
        } else {
            logger.warn("❌ JWT token is invalid.");
            return ResponseEntity.status(401).body("JWT token is invalid.");
        }

    }

    @Operation(summary = "Forgot Password", description = "Allows users to reset their password by providing their email and a new password.")
    @PutMapping("/forgotPassword/{email}")
    public ResponseEntity<String> forgotPassword(@PathVariable String email, @RequestBody Map<String, String> requestBody) {
        String newPassword = requestBody.get("password");
        String response = authService.forgotPassword(email, newPassword);
        return response.startsWith("Password has been changed") ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @Operation(summary = "Reset Password", description = "Allows authenticated users to change their password by providing the current and new password.")
    @PutMapping("/resetPassword/{email}")
    public ResponseEntity<String> resetPassword(@PathVariable String email, @RequestParam String currentPassword, @RequestBody String newPassword) {
        String response = authService.resetPassword(email, currentPassword, newPassword);
        return response.equals("Password reset successfully!") ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }
}