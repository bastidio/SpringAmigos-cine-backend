package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.ImagenProducto;
import com.uade.tpo.demo.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Long> {
    // Para validar que una imagen pertenece al producto antes de borrarla
    Optional<ImagenProducto> findByIdAndProducto(Long id, Producto producto);
}
