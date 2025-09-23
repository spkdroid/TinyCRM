package com.spkd.tinycrm.tinyos.dto;

import com.spkd.tinycrm.tinyos.entity.User;
import java.time.LocalDateTime;

public class LoginResponse {
    
    private String sessionToken;
    private User user;
    private LocalDateTime expiresAt;
    private String message;
    private boolean success;

    // Constructors
    public LoginResponse() {}

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResponse(String sessionToken, User user, LocalDateTime expiresAt) {
        this.sessionToken = sessionToken;
        this.user = user;
        this.expiresAt = expiresAt;
        this.success = true;
        this.message = "Login successful";
    }

    // Getters and Setters
    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
