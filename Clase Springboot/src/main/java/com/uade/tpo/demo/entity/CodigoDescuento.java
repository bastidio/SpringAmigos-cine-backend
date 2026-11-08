package com.uade.tpo.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "codigo_descuento")
public class CodigoDescuento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Siempre en mayusculas y sin espacios (ver CodigoDescuentoServiceIMPL.normalizar).
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    // Entero de 1 a 100.
    @Column(nullable = false)
    private Integer porcentaje;

    // Baja logica, igual que Producto.
    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean activo = true;

    // Opcional: el codigo vale hasta el fin de ese dia. null = no vence.
    @Column
    private LocalDate fechaVencimiento;
}
