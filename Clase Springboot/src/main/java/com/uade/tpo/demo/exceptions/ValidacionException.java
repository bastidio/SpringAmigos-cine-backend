package com.uade.tpo.demo.exceptions;

import java.util.Map;

// Validacion manual de los request (sin bean validation / @Valid).
// Lleva el detalle campo -> motivo para armar un 400 informativo en
// GlobalExceptionHandler.
public class ValidacionException extends RuntimeException {

    private final transient Map<String, String> errores;

    public ValidacionException(Map<String, String> errores) {
        super("Datos invalidos");
        this.errores = errores;
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
