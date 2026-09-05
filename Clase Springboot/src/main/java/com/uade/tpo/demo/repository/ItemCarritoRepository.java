package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.ItemCarrito;
import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.entity.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
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

    // Para detectar que una butaca ya esta en el carrito y no duplicarla
    Optional<ItemCarrito> findByCarritoAndAsiento(Carrito carrito, Asiento asiento);
}