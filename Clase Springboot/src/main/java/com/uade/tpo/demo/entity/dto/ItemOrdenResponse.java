package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

// Un producto comprado dentro de una orden, ya resuelto para el frontend.
@Data
@AllArgsConstructor
public class ItemOrdenResponse {
    private String nombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
