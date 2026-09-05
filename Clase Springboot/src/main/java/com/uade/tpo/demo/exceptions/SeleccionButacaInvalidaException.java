package com.uade.tpo.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Seleccion de butaca invalida: falta la funcion, la butaca no pertenece a su sala, ya esta en el carrito, o el carrito tiene butacas de otra funcion")
public class SeleccionButacaInvalidaException extends Exception {

}