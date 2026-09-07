package com.uade.tpo.demo.entity.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

// Publico: solo ids de asiento, nada de la orden ni del comprador.
@Data
@AllArgsConstructor
public class OcupacionFuncionResponse {
    private List<Long> asientosOcupados;
}
