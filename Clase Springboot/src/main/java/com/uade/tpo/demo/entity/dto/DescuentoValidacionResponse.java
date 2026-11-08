package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

// Resultado de validar un codigo contra el carrito del usuario. Si no es valido, solo vienen valido y mensaje.
@Data
@AllArgsConstructor
public class DescuentoValidacionResponse {
    private boolean valido;
    private String mensaje;
    private String codigo;
    private Integer porcentaje;
    private BigDecimal subtotal;
    private BigDecimal montoDescuento;
    private BigDecimal total;
}
