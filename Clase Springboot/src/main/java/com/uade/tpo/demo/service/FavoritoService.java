package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.dto.FavoritoResponse;
import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;

import java.util.List;

public interface FavoritoService {

    // Solo los favoritos del usuario autenticado.
    List<FavoritoResponse> getMisFavoritos(Usuario usuario);

    // Idempotente: si ya era favorita no hace nada.
    void agregar(Usuario usuario, Long peliculaId) throws PeliculaNotFoundException;

    // Idempotente: si no era favorita no hace nada.
    void quitar(Usuario usuario, Long peliculaId);
}
