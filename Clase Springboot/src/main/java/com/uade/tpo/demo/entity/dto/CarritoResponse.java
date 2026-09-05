package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

// Respuesta del carrito con todo lo que el frontend necesita para dibujarlo:
// los items y el total ya calculado. La entidad Carrito sola no alcanza porque
// no tiene la coleccion de items mapeada.
@Data
@AllArgsConstructor
public class CarritoResponse {
    private Long carritoId;
    private Long funcionId;
    private List<ItemCarritoResponse> items;
    private BigDecimal total;
}