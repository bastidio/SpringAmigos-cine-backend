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
@Table(name = "orden")

public class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @Column
    private LocalDateTime fecha;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @Column
    private String estado;

    // Foto del descuento al momento de la compra (no es FK): el historial no cambia si el codigo se edita o desactiva.
    // null en las ordenes sin codigo y en las anteriores a esta funcionalidad.
    @Column
    private String codigoDescuento;

    @Column
    private Integer porcentajeDescuento;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal montoDescuento;
}
