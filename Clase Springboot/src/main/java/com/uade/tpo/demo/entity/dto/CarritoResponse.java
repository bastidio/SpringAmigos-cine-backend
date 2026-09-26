package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

// Carrito no tiene mapeada la coleccion de items, por eso este DTO en vez de la entidad.
@Data
@AllArgsConstructor
public class CarritoResponse {
    private Long carritoId;
    private Long funcionId;
    private List<ItemCarritoResponse> items;
    private BigDecimal total;
    private LocalDateTime expiraEn;
    private boolean vencido;
}
