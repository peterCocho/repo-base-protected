package com.churninsight_dev.backend_api.exception;

import org.springframework.security.access.AccessDeniedException;

import com.churninsight_dev.backend_api.dto.AuthErrorResponse;
import com.churninsight_dev.backend_api.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Captura excepciones en toda la aplicación y las devuelve en un formato JSON limpio.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja accesos denegados por roles insuficientes (Error 403).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Herramienta no disponible. Actualiza a PREMIUM para tener acceso a predicciones múltiples.");
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    /**
     * Maneja errores de validación (ej. campos obligatorios faltantes).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Extraemos cada error de validación del objeto Exception
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ErrorResponse errorRes = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Error de Validación",
                "Los datos enviados no son válidos",
                errors
        );
        return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja cualquier otro error inesperado (Error 500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse errorRes = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor",
                ex.getMessage(),
                null);
        return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(EmailAlreadyExistsException.class)
public ResponseEntity<AuthErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
    AuthErrorResponse errorRes = new AuthErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            "Error de Registro",
            ex.getMessage(),
            "email",
            "EMAIL_ALREADY_EXISTS"
    );
    return new ResponseEntity<>(errorRes, HttpStatus.CONFLICT);
}

@ExceptionHandler(PasswordMismatchException.class)
public ResponseEntity<AuthErrorResponse> handlePasswordMismatch(PasswordMismatchException ex) {
    AuthErrorResponse errorRes = new AuthErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Error de Registro",
            ex.getMessage(),
            "repeatPassword",
            "PASSWORD_MISMATCH"
    );
    return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
}


}
