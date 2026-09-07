package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

// Entrada resuelta para el ticket, sin arrastrar la Orden completa anidada.
@Data
@AllArgsConstructor
public class EntradaResponse {
    private Long id;
    private String pelicula;
    private String sala;
    private LocalDateTime horario;
    private Integer fila;
    private Integer numero;
    private BigDecimal precio;
}
