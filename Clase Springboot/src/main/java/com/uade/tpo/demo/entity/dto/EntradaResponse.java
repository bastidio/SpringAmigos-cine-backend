package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

// Una entrada ya resuelta para mostrar como ticket: lo necesario para
// identificarla y mostrarla, sin arrastrar la Orden completa anidada.
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
