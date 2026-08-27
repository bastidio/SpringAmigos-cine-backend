package com.uade.tpo.demo.entity;

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
@Table(name = "producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  
    @ManyToOne
    @JoinColumn(name = "categoria_id", referencedColumnName = "id")
    private Categoria categoria;

    @Column
    private String nombre;

    @Column
    private String descripcion;

    @Column
    private Float precio;

    @Column
    private Integer stock;

    @Column
    private Float descuento;

    @Column
    private String imagen_url;
}