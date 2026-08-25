package com.uade.tpo.marketplace.entity;

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
@Table(name = "entrada")
public class Entrada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @ManyToOne
    @JoinColumn(name = "orden_id", referencedColumnName = "id")
    private Long orden_id;

    @Column
    @ManyToOne
    @JoinColumn(name = "funcion_id", referencedColumnName = "id")
    private Long funcion_id;

    @Column
    @ManyToOne
    @JoinColumn(name = "asiento_id", referencedColumnName = "id")
    private Long asiento_id;

    @Column
    private Float precio;
}
