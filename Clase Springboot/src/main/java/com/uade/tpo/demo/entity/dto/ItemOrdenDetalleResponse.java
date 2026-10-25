package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

// Item de orden con la forma que espera el panel admin: { cantidad, precio_unitario, producto: { id, nombre } }.
@Data
@AllArgsConstructor
public class ItemOrdenDetalleResponse {
    private Integer cantidad;
    private BigDecimal precio_unitario;
    private ProductoResumen producto;

    @Data
    @AllArgsConstructor
    public static class ProductoResumen {
        private Long id;
        private String nombre;
    }
}
