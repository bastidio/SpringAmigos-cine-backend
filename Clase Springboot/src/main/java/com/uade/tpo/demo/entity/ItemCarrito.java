package com.uade.tpo.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
@Table(name = "item_carrito")
public class ItemCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne
    @JoinColumn(name = "carrito_id", referencedColumnName = "id")
    private Carrito carrito;

   
    @ManyToOne
    @JoinColumn(name = "producto_id", referencedColumnName = "id")
    private Producto producto;

   
    @ManyToOne
    @JoinColumn(name = "asiento_id", referencedColumnName = "id")
    private Asiento asiento;

    @Column
    private Integer cantidad;

    // Momento en que se reservó la butaca; null para ítems de producto.
    @Column
    private LocalDateTime reservadoEn;

    public static final int MINUTOS_RESERVA = 15;

    public boolean reservaVencida() {
        return this.reservadoEn != null &&
               this.reservadoEn.plusMinutes(MINUTOS_RESERVA).isBefore(LocalDateTime.now());
    }
}
