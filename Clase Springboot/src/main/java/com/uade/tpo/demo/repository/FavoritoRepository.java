package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    // Mas recientes primero.
    List<Favorito> findByUsuarioIdOrderByAgregadoEnDesc(Long usuarioId);

    // Para avisar a quienes la tienen en favoritos cuando se crea una funcion.
    List<Favorito> findByPeliculaId(Long peliculaId);

    boolean existsByUsuarioIdAndPeliculaId(Long usuarioId, Long peliculaId);

    void deleteByUsuarioIdAndPeliculaId(Long usuarioId, Long peliculaId);
}
