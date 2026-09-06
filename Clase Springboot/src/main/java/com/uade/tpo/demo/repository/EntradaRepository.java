package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Entrada;
import com.uade.tpo.demo.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Long> {

    // Todas queries derivadas: los campos de Entrada ya no tienen "_" (orden,
    // funcion, asiento), asi que Spring Data puede navegar orden.usuario.id,
    // funcion.id, etc. sin ambiguedad.

    // Entradas de un usuario: se navega orden -> usuario -> id.
    List<Entrada> findByOrdenUsuarioId(Long usuarioId);

    // Entradas de una funcion. El endpoint publico de ocupacion solo devuelve
    // los ids de asiento (los saca el service); nunca se serializa la Entrada
    // entera, asi que la orden y el comprador no se exponen.
    List<Entrada> findByFuncionId(Long funcionId);

    // Entradas emitidas por una orden. Se usa al cancelar: hay que borrarlas
    // para liberar la butaca (el UNIQUE (funcion_id, asiento_id) la mantiene
    // bloqueada mientras exista la fila).
    List<Entrada> findByOrden(Orden orden);
}
