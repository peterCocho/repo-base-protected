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
    private Map<String, String> validationErrors; // Aquí guardaremos qué campo falló y por qué
}