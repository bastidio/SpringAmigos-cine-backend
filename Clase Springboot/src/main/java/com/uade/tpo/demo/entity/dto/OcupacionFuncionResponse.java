package com.uade.tpo.demo.entity.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

// Respuesta publica de ocupacion de una funcion: solo ids de asiento, para que
// el frontend dibuje el mapa de sala. Nada de la orden ni del comprador.
@Data
@AllArgsConstructor
public class OcupacionFuncionResponse {
    private List<Long> asientosOcupados;
}
