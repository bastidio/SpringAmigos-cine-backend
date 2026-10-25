package com.uade.tpo.demo.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Comprador de una orden para el panel admin: sin email, password ni rol.
@Data
@AllArgsConstructor
public class UsuarioResumenResponse {
    private Long id;
    private String nombre;
}
