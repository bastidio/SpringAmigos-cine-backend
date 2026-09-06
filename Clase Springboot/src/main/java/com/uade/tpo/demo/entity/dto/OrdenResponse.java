package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

// Respuesta de una orden con el detalle de la compra: la entidad Orden sola
// no alcanza porque no tiene mapeados los ItemOrden ni las Entradas.
@Data
@AllArgsConstructor
public class OrdenResponse {
    private Long id;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String estado;
    private List<ItemOrdenResponse> productos;
    private List<EntradaResponse> entradas;
}
