package com.uade.tpo.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "El ítem de carrito solicitado no existe o no pertenece al carrito del usuario")
public class ItemCarritoNotFoundException extends Exception {

}
