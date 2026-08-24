package com.uade.tpo.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
//razon la cual se rechaza la solicitud, en este caso, "La categoria que se intenta agregar esta duplicada"
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "La categoria que se intenta agregar esta duplicada")
public class CategoryDuplicateException extends Exception {

}
