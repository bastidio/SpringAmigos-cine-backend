package com.uade.tpo.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST,
        reason = "La cantidad debe ser un numero mayor a cero")
public class CantidadInvalidaException extends Exception {

}