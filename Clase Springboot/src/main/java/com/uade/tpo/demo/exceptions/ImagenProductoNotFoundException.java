package com.uade.tpo.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "La imagen solicitada no existe o no pertenece al producto")
public class ImagenProductoNotFoundException extends Exception {

}
