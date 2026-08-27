package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Pelicula;
import java.util.List;
import java.util.Optional;

public interface PeliculaService {
    List<Pelicula> getPeliculas();
    Optional<Pelicula> getPeliculaById(Long id);
    Pelicula createPelicula(String titulo, String sinopsis, Integer duracion, String clasificacion, String idioma, String posterUrl);
}