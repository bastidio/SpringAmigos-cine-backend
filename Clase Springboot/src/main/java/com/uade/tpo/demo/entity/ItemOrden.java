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
@Table(name = "item_orden")
public class ItemOrden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne
    @JoinColumn(name = "orden_id", referencedColumnName = "id")
    private Orden orden;

    
    @ManyToOne
    @JoinColumn(name = "producto_id", referencedColumnName = "id")
    private Producto producto;

    @Column
    private Integer cantidad;

    @Column
    private Float precio_unitario;
}
