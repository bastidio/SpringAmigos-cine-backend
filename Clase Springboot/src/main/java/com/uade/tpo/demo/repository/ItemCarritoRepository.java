package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    @Transactional//borra todos los items del carrito
    void deleteAllByCarrito(Carrito carrito);
    // Spring Data JPA arma automáticamente el "SELECT * FROM item_carrito WHERE carrito_id = ?"
    List<ItemCarrito> findAllByCarrito(Carrito carrito);
}