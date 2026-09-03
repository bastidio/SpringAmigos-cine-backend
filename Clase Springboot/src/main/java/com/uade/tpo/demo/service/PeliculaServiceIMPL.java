package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Pelicula;
import com.uade.tpo.demo.exceptions.PeliculaDuplicateException;
import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;
import com.uade.tpo.demo.repository.PeliculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeliculaServiceIMPL implements PeliculaService {

    @Autowired
    private PeliculaRepository peliculaRepository; 

    @Override
    public List<Pelicula> getPeliculas() {
        return peliculaRepository.findAll();
    }

    @Override
    public Pelicula getPeliculaById(Long id) throws PeliculaNotFoundException {
        return peliculaRepository.findById(id)
                .orElseThrow(PeliculaNotFoundException::new);
    }

    @Override
    public Pelicula createPelicula(String titulo, String sinopsis, Integer duracion, String clasificacion, String idioma, String posterUrl)
            throws PeliculaDuplicateException {
        boolean peliculaDuplicada = peliculaRepository.findAll().stream()
                .anyMatch(pelicula -> pelicula.getTitulo() != null
                        && pelicula.getTitulo().equalsIgnoreCase(titulo));

        if (peliculaDuplicada) {
            throw new PeliculaDuplicateException();
        }

        Pelicula nuevaPelicula = new Pelicula();
        nuevaPelicula.setTitulo(titulo);
        nuevaPelicula.setSinopsis(sinopsis);
        nuevaPelicula.setDuracion(duracion);
        nuevaPelicula.setClasificacion(clasificacion);
        nuevaPelicula.setIdioma(idioma);
        nuevaPelicula.setPoster_url(posterUrl);
        
        return peliculaRepository.save(nuevaPelicula);
    }
}