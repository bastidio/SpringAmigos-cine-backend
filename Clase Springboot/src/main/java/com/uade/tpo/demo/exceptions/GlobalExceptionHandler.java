package com.uade.tpo.demo.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.uade.tpo.demo.entity.dto.ErrorResponse;

// Traduce excepciones no anotadas a respuestas JSON consistentes (ErrorResponse).
// Las excepciones de dominio que ya llevan @ResponseStatus (ProductoNotFoundException,
// OrdenNotFoundException, etc.) las sigue resolviendo Spring por su cuenta: aca no
// hay un handler de Exception.class que las intercepte y las degrade a 500.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Falla de @Valid: junta cada campo invalido con su motivo.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errores.put(fe.getField(), fe.getDefaultMessage());
        }
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Datos invalidos",
                "El cuerpo del request no paso las validaciones",
                errores);
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

    // Orden inexistente. Se maneja aca (y no solo con el @ResponseStatus de la
    // excepcion) para responder el mismo ErrorResponse que el resto y evitar el
    // re-despacho a /error con el cuerpo por defecto de Spring Boot.
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
