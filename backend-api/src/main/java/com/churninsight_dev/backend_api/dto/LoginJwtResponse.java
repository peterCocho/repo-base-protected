package com.churninsight_dev.backend_api.dto;

public class LoginJwtResponse extends LoginResponse {
    private String token;

    // ...código existente...
public LoginJwtResponse(String message, String email, String username, boolean status, String token) {
    super(message, email, username, status);
    this.token = token;
}
// ...código existente...

    public String getToken() { return token; }
}
