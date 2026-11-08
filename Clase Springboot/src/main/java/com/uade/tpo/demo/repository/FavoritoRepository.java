package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    // Mas recientes primero.
    List<Favorito> findByUsuarioIdOrderByAgregadoEnDesc(Long usuarioId);

    boolean existsByUsuarioIdAndPeliculaId(Long usuarioId, Long peliculaId);

    void deleteByUsuarioIdAndPeliculaId(Long usuarioId, Long peliculaId);
}
