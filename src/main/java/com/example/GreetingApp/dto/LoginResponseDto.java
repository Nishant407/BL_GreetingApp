package com.example.GreetingApp.dto;

public class LoginResponseDto {
    private String message;
    private String token;

    // Constructor to initialize both fields
    public LoginResponseDto(String message, String token) {
        this.message = message;
        this.token = token;
    }

    // Default constructor (optional, but useful if you need to instantiate without values)
    public LoginResponseDto() {}

    // Getters
    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    // Setters (optional if you need to modify the object after creation)
    public void setMessage(String message) {
        this.message = message;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
