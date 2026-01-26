package com.churninsight_dev.backend_api.dto;

public class LoginResponse {
    private String message;
    private String email;
    private String username;
    private boolean status;

    public LoginResponse(String message, String email, String username, boolean status) {
        this.message = message;
        this.email = email;
        this.username = username;
        this.status = status;
    }

    public String getMessage() { return message; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public boolean getStatus() { return status; }
}
