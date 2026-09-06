package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

// Un item del carrito ya resuelto para el frontend: no importa si por dentro
// es un producto o una butaca, sale con la misma forma. El itemId es el que
// necesita el front para el DELETE y el PUT sobre /api/carritos/items/{id}.
@Data
@AllArgsConstructor
public class ItemCarritoResponse {
    private Long itemId;
    private String tipo;
    private Long referenciaId;
    private String nombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}