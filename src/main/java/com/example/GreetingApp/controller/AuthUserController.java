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
import jakarta.validation.Valid;  // Use jakarta.validation.Valid instead.
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        LoginResponseDto response = authService.loginUser(loginDto);

        if (response.getToken() == null) { // Login failed
            logger.warn("Login failed for user: {}", loginDto.getUsername());
            return ResponseEntity.status(401).body(response);  // Unauthorized
        }

        logger.info("Login successful for user: {}", loginDto.getUsername());
        return ResponseEntity.ok(response);  // OK with token
    }

    @Operation(summary = "Register a new user", description = "Saves user details and returns success message.")
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserDto userDto) {
        logger.info("🚀 Register Endpoint Hit: {}", userDto.getEmail());

        String response = authService.registerUser(userDto);

        // Return appropriate response based on the registration result
        if (response.startsWith("User registered successfully")) {
            return ResponseEntity.status(201).body(response);  // Created
        } else {
            logger.error("User registration failed: {}", response);
            return ResponseEntity.status(400).body(response);  // Bad Request
        }
    }

    @Operation(summary = "Test Authenticated Endpoint", description = "Tests if the provided JWT token is valid.")
    @GetMapping("/test")
    public ResponseEntity<String> testAuthenticatedEndpoint(@RequestHeader("Authorization") String authHeader) {
        // Remove "Bearer " prefix from the token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Check the token validity
            boolean isValid = authService.validateToken(token);

            if (isValid) {
                return ResponseEntity.ok("JWT token is valid.");
            } else {
                return ResponseEntity.status(401).body("JWT token is invalid.");
            }
        } else {
            return ResponseEntity.status(400).body("Authorization token is missing or malformed.");
        }
    }
}
