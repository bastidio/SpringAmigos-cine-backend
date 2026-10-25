package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsientoRepository extends JpaRepository<Asiento, Long> {

    // Ordenados por fila y numero para que el front arme el mapa de butacas sin reordenar.
    List<Asiento> findBySalaIdOrderByFilaAscNumeroAsc(Long salaId);
}
