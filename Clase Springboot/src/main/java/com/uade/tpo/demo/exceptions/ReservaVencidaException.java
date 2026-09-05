package com.uade.tpo.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "La reserva de la butaca venció, volvé a seleccionarla")
public class ReservaVencidaException extends Exception {
}
