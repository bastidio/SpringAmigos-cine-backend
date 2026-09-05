package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Entrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Long> {

    // Se usa @Query en vez de query derivada porque los campos de Entrada se llaman
    // orden_id / funcion_id / asiento_id (con guion bajo), y el "_" es un caracter
    // reservado de Spring Data para marcar el corte entre propiedades anidadas. Una
    // query derivada tipo findByFuncion_idId sería ambigua; JPQL referencia el campo
    // por su nombre Java real y no tiene ese problema. Cuando se renombren los campos
    // (rama de fixes), esto puede volver a métodos derivados.

    // Entradas de un usuario: se navega orden_id -> usuario -> id.
    @Query("SELECT e FROM Entrada e WHERE e.orden_id.usuario.id = :usuarioId")
    List<Entrada> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Ocupación de una función: solo los ids de asiento, sin traer la orden ni
    // el usuario (evita exponer datos del comprador en el endpoint publico).
    @Query("SELECT e.asiento_id.id FROM Entrada e WHERE e.funcion_id.id = :funcionId")
    List<Long> findAsientoIdsByFuncionId(@Param("funcionId") Long funcionId);
}