package com.uade.tpo.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "funcion")
public class Funcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne
    @JoinColumn(name = "pelicula_id", referencedColumnName = "id")
    private Pelicula pelicula;

  
    @ManyToOne
    @JoinColumn(name = "sala_id", referencedColumnName = "id")
    private Sala sala;

    @Column
    private LocalDateTime horario;

    @Column
    private String idioma;

    @Column
    private String formato;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio_base;
}
