package com.uade.tpo.marketplace.entity;

import java.sql.Date;

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

    @Column
    @ManyToOne
    @JoinColumn(name = "pelicula_id", referencedColumnName = "id")
    private Long pelicula;

    @Column
    @ManyToOne
    @JoinColumn(name = "sala_id", referencedColumnName = "id")
    private Long sala;

    @Column
    private Date horario;

    @Column
    private String idioma;

    @Column
    private String formato;

    @Column
    private Float precio_base;
}
