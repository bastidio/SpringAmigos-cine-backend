package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Pelicula;
import com.uade.tpo.demo.exceptions.PeliculaDuplicateException;
import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;

import java.util.List;

public interface PeliculaService {
    List<Pelicula> getPeliculas();
    Pelicula getPeliculaById(Long id) throws PeliculaNotFoundException;
    Pelicula createPelicula(String titulo, String sinopsis, Integer duracion, String clasificacion, String idioma, String posterUrl)
            throws PeliculaDuplicateException;
}