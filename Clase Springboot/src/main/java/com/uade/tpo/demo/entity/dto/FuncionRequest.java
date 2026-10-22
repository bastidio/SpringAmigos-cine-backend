package com.uade.tpo.demo.entity.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

// El panel admin manda pelicula_id / sala_id: el alias acepta ambos formatos sin renombrar los campos.
@Data
public class FuncionRequest {
    @JsonAlias("pelicula_id")
    private Long peliculaId;
    @JsonAlias("sala_id")
    private Long salaId;
    private LocalDateTime horario;
    private String idioma;
    private String formato;
    private BigDecimal precio_base;
}