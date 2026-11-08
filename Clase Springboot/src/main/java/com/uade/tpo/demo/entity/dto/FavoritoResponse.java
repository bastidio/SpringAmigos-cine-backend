package com.uade.tpo.demo.entity.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

// Favorito resuelto para el front, sin arrastrar el Usuario.
@Data
@AllArgsConstructor
public class FavoritoResponse {
    private Long peliculaId;
    private String titulo;
    private String poster_url;
    private String clasificacion;
    private LocalDateTime agregadoEn;
}
