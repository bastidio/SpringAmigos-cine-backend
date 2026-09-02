package com.uade.tpo.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "La función no tiene disponibilidad de asientos")
public class FuncionSinDisponibilidadException extends Exception {

}