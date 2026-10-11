package com.uade.tpo.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "El tiempo para confirmar la compra vencio, el carrito fue vaciado")
public class CarritoExpiradoException extends Exception {

}
