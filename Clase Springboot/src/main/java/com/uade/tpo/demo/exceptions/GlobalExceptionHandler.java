package com.uade.tpo.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.uade.tpo.demo.entity.dto.ErrorResponse;


// Maneja errores de validacion y de negocio de manera centralizada, devolviendo un JSON consistente para el frontend.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validacion manual de los request (ver ProductoServiceIMPL): campo -> motivo.
    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(ValidacionException ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Datos invalidos",
                "El cuerpo del request no paso las validaciones",
                ex.getErrores());
        return ResponseEntity.badRequest().body(body);
    }

    // JSON mal formado o tipo incompatible en el cuerpo.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Cuerpo ilegible",
                "El cuerpo del request no es un JSON valido");
        return ResponseEntity.badRequest().body(body);
    }
    // La orden solicitada no existe.
    @ExceptionHandler(OrdenNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrdenNotFound(OrdenNotFoundException ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "No encontrado",
                "La orden solicitada no existe");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // El solicitante no es el dueño de la orden ni ADMIN.
    @ExceptionHandler(OrdenAccesoDenegadoException.class)
    public ResponseEntity<ErrorResponse> handleOrdenAccesoDenegado(OrdenAccesoDenegadoException ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Acceso denegado",
                "No tenes permiso para acceder a esta orden");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // Estado de negocio invalido (ej: cancelar una orden que no esta CONFIRMADA).
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflicto de estado",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // Argumento invalido que no viene de bean validation.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Argumento invalido",
                ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }
}
