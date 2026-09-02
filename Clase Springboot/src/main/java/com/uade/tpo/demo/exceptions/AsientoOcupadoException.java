package com.uade.tpo.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "El asiento seleccionado ya se encuentra ocupado")
public class AsientoOcupadoException extends Exception {

}