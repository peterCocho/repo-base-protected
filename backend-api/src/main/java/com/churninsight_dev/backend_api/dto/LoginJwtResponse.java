package com.churninsight_dev.backend_api.dto;

public class LoginJwtResponse extends LoginResponse {
    private String token;

    public LoginJwtResponse(String message, String email, String username, int status, String token) {
        super(message, email, username, status);
        this.token = token;
    }

    public String getToken() { return token; }
}
