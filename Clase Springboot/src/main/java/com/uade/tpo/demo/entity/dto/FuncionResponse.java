package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.uade.tpo.demo.entity.Pelicula;
import com.uade.tpo.demo.entity.Sala;

import lombok.AllArgsConstructor;
import lombok.Data;

// Misma forma que la entidad Funcion (el cliente ya la usa) mas entradasVendidas para el panel admin.
@Data
@AllArgsConstructor
public class FuncionResponse {
    private Long id;
    private Pelicula pelicula;
    private Sala sala;
    private LocalDateTime horario;
    private String idioma;
    private String formato;
    private BigDecimal precio_base;
    private long entradasVendidas;
}
