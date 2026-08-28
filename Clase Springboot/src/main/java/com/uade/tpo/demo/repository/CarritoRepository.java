package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    
    // Cambiamos "Comprador" por "Usuario"
    Optional<Carrito> findByUsuarioId(Long usuarioId);
    
    
}