package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.ItemCarrito;
import com.uade.tpo.demo.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    @Transactional//borra todos los items del carrito
    void deleteAllByCarrito(Carrito carrito);
    // Spring Data JPA arma automáticamente el "SELECT * FROM item_carrito WHERE carrito_id = ?"
    List<ItemCarrito> findAllByCarrito(Carrito carrito);

    // Para validar que un item pertenece al carrito del usuario antes de tocarlo
    Optional<ItemCarrito> findByIdAndCarrito(Long id, Carrito carrito);

    // Para consolidar cantidades cuando se agrega un producto que ya está en el carrito
    Optional<ItemCarrito> findByCarritoAndProducto(Carrito carrito, Producto producto);

    // ¿La butaca está reservada por OTRO carrito con la reserva todavía vigente?
    Optional<ItemCarrito> findByAsiento_IdAndCarrito_IdNotAndReservadoEnAfter(
            Long asientoId, Long carritoId, LocalDateTime limite);

    // Ítems de butaca cuya reserva ya venció (para la limpieza periódica)
    List<ItemCarrito> findByAsientoIsNotNullAndReservadoEnBefore(LocalDateTime limite);
}