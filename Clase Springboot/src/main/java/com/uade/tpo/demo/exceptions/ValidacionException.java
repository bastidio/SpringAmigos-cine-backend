package com.uade.tpo.demo.exceptions;

import java.util.Map;

// Validacion manual (sin @Valid), lleva el detalle campo -> motivo para el 400.
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
