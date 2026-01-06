package com.churninsight.backend_api.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Estructura estandarizada para todas las respuestas de error de la API.
 */
@Data
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private Map<String, String> validationErrors;

    public ErrorResponse() {}

    public ErrorResponse(String error) {
        this.error = error;
    }

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, Map<String, String> validationErrors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.validationErrors = validationErrors;
    }
}