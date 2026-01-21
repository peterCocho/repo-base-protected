package com.churninsight_dev.backend_api.dto;

public class VerificationRequest {
    private String email;
    private String verification;

    public VerificationRequest() {}

    public VerificationRequest(String email, String verification) {
        this.email = email;
        this.verification = verification;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getVerification() { return verification; }
    public void setVerification(String verification) { this.verification = verification; }
}
