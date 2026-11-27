package com.uade.tpo.demo.entity.dto;

// Datos del aviso de nueva funcion, ya sacados de las entidades: el mail se manda en otro hilo.
public record NuevaFuncionMail(
        String email,
        String pelicula,
        Long peliculaId) {
}
