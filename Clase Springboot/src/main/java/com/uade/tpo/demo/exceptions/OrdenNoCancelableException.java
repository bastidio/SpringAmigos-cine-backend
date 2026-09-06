package com.uade.tpo.demo.exceptions;

// Excepcion de negocio: la orden no puede cancelarse (ya paso la funcion o ya fue cancelada).
public class OrdenNoCancelableException extends RuntimeException {

    public OrdenNoCancelableException(String mensaje) {
        super(mensaje);
    }
}
