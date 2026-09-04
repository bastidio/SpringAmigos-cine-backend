package com.uade.tpo.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "No tenés permiso para acceder a esta entrada")
public class EntradaAccesoDenegadoException extends Exception {

}