package com.uade.tpo.demo.entity.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;

// DTO unico de error para todas las respuestas de fallo que pasan por
// GlobalExceptionHandler. Formato estable para que el front no dependa
// del cuerpo de error por defecto de Spring Boot.
@Data
public class ErrorResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final String message;
    // Solo se completa en errores de validacion: campo -> motivo.
    private final Map<String, String> validationErrors;

    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, null);
    }

    public ErrorResponse(int status, String error, String message, Map<String, String> validationErrors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.validationErrors = validationErrors;
    }
}
