package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.CodigoDescuento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodigoDescuentoRepository extends JpaRepository<CodigoDescuento, Long> {
    Optional<CodigoDescuento> findByCodigo(String codigo);
}
