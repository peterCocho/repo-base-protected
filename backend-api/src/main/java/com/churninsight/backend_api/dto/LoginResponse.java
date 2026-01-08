package com.churninsight.backend_api.dto;

public class LoginResponse {
    private String message;
    private String email;
    private String username;
    private int status;

    public LoginResponse(String message, String email, String username, int status) {
        this.message = message;
        this.email = email;
        this.username = username;
        this.status = status;
    }

    public String getMessage() { return message; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public int getStatus() { return status; }
}
