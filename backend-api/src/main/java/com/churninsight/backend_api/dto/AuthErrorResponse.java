package com.churninsight.backend_api.dto;

import java.time.LocalDateTime;

public class AuthErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String campo;
    private String codigo;

    public AuthErrorResponse(LocalDateTime timestamp, int status, String error, String message, String campo, String codigo) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.campo = campo;
        this.codigo = codigo;
    }

    // Getters y setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getCampo() { return campo; }
    public void setCampo(String campo) { this.campo = campo; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}
