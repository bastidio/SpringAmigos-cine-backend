package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Entrada;
import com.uade.tpo.demo.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Long> {

    // Campos de Entrada sin "_" para que Spring Data navegue orden.usuario.id sin ambiguedad.
    List<Entrada> findByOrdenUsuarioId(Long usuarioId);

    // El service solo saca los ids de asiento, nunca se serializa la Entrada entera.
    List<Entrada> findByFuncionId(Long funcionId);

    // Se usa al cancelar: hay que borrarlas para liberar la butaca (las mantiene el UNIQUE).
    List<Entrada> findByOrden(Orden orden);
}
