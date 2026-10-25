package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Pelicula;
import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;

import java.util.List;
import java.util.Optional;

public interface PeliculaService {
    List<Pelicula> getPeliculas();
    Optional<Pelicula> getPeliculaById(Long id);
    Pelicula createPelicula(String titulo, String sinopsis, Integer duracion, String clasificacion, String idioma, String posterUrl);
    Pelicula updatePelicula(Long id, String titulo, String sinopsis, Integer duracion, String clasificacion, String idioma, String posterUrl)
            throws PeliculaNotFoundException;

    // 409 (IllegalStateException) si la pelicula tiene funciones.
    void deletePelicula(Long id) throws PeliculaNotFoundException;
}
