package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

// Producto o butaca, sale con la misma forma. itemId es el que usa el front para el DELETE/PUT.
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