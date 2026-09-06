package com.uade.tpo.demo.entity;

import java.math.BigDecimal;

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

@Entity
@Data
@Table(name = "entrada", uniqueConstraints = @UniqueConstraint(columnNames = {"funcion_id", "asiento_id"}))
public class Entrada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    // La columna en la DB sigue llamandose orden_id / funcion_id / asiento_id
    // (ver @JoinColumn); solo cambia el nombre del campo Java para poder usar
    // queries derivadas sin que Spring Data confunda el "_" con un salto anidado.
    @ManyToOne
    @JoinColumn(name = "orden_id", referencedColumnName = "id")
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "funcion_id", referencedColumnName = "id")
    private Funcion funcion;

    @ManyToOne
    @JoinColumn(name = "asiento_id", referencedColumnName = "id")
    private Asiento asiento;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;
}
