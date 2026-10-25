package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Long> {

    // Funciones de un dia: [desde, hasta) para no depender de la hora 23:59:59.
    List<Funcion> findByHorarioGreaterThanEqualAndHorarioLessThanOrderByHorarioAsc(LocalDateTime desde,
            LocalDateTime hasta);

    // Para no borrar una pelicula que todavia tiene funciones (la FK lo impediria con un 500).
    boolean existsByPeliculaId(Long peliculaId);
}
