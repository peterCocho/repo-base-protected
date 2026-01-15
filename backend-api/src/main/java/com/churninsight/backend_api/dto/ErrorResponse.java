package com.churninsight.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Estructura estandarizada para todas las respuestas de error de la API.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status; // El código HTTP (400, 404, 500)
    private String error; // Nombre corto del error (ej: "Bad Request")
    private String message; // Explicación para el humano
    private Map<String, String> validationErrors; // Aquí guardaremos qué campo falló y por qué
}