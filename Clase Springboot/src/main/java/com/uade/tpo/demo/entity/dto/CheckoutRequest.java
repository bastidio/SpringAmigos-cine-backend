package com.uade.tpo.demo.entity.dto;

import lombok.Data;

// Solo el codigo: el backend recalcula porcentaje y total, nunca los recibe del front.
@Data
public class CheckoutRequest {
    private String codigoDescuento;
}
