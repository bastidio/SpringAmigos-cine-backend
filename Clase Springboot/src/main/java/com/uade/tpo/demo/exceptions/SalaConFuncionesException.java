package com.uade.tpo.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Una sala con funciones no se reconfigura: sus butacas pueden tener entradas vendidas.
@ResponseStatus(code = HttpStatus.CONFLICT, reason = "La sala tiene funciones asignadas: no se pueden quitar sus butacas")
public class SalaConFuncionesException extends Exception {

}
