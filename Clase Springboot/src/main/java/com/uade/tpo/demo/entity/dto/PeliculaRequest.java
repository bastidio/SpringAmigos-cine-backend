package com.uade.tpo.demo.entity.dto;

import lombok.Data;

@Data
public class PeliculaRequest {
    private String titulo;
    private String sinopsis;
    private Integer duracion;
    private String clasificacion;
    private String idioma;
    private String poster_url;
}