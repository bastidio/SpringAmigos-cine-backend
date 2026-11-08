package com.uade.tpo.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

// Pelicula marcada como favorita por un usuario (una sola vez por par usuario+pelicula).
@Entity
@Data
@Table(name = "favorito", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "pelicula_id"}))
public class Favorito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "pelicula_id", referencedColumnName = "id")
    private Pelicula pelicula;

    @Column
    private LocalDateTime agregadoEn;
}
