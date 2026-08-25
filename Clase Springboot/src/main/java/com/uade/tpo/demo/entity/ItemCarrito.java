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
@Table(name = "item_carrito")
public class ItemCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @ManyToOne
    @JoinColumn(name = "carrito_id", referencedColumnName = "id")
    private Long carrito;

    @Column
    @ManyToOne
    @JoinColumn(name = "producto_id", referencedColumnName = "id")
    private Long producto;

    @Column
    @ManyToOne
    @JoinColumn(name = "asiento_id", referencedColumnName = "id")
    private Long asiento;

    @Column
    private Integer cantidad;
}
