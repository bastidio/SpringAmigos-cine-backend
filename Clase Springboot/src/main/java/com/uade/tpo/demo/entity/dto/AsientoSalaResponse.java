package com.uade.tpo.demo.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Butaca para el mapa de una sala, sin la Sala anidada (ya se sabe por el path). fila: 1 = A.
@Data
@AllArgsConstructor
public class AsientoSalaResponse {
    private Long id;
    private Integer fila;
    private Integer numero;
}
