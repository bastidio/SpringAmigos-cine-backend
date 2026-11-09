package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Entrada;
import com.uade.tpo.demo.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Long> {

    // Campos de Entrada sin "_" para que Spring Data navegue orden.usuario.id sin ambiguedad.
    List<Entrada> findByOrdenUsuarioId(Long usuarioId);

    // El service solo saca los ids de asiento, nunca se serializa la Entrada entera.
    List<Entrada> findByFuncionId(Long funcionId);

    // Entradas de una orden (para armar el detalle de la orden).
    List<Entrada> findByOrden(Orden orden);

    // Entradas vendidas de una funcion.
    long countByFuncionId(Long funcionId);

    // Entradas activas: orden en el estado dado (COMPLETADA) y funcion que todavia no paso.
    long countByOrdenUsuarioIdAndOrdenEstadoAndFuncionHorarioAfter(Long usuarioId, String estado,
            LocalDateTime ahora);
}
