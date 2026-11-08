package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {

    // "En cartelera" se deriva: la pelicula tiene al menos una funcion con horario >= ahora.
    @Query("SELECT p FROM Pelicula p WHERE EXISTS "
            + "(SELECT f.id FROM Funcion f WHERE f.pelicula = p AND f.horario >= :ahora)")
    List<Pelicula> findEnCartelera(@Param("ahora") LocalDateTime ahora);
}