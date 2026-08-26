package com.uade.tpo.demo.service;

import com.uade.tpo.marketplace.entity.Pelicula;
import com.uade.tpo.marketplace.repository.PeliculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PeliculaServiceIMPL implements PeliculaService {

    @Autowired
    private PeliculaRepository peliculaRepository; 

    @Override
    public List<Pelicula> getPeliculas() {
        return peliculaRepository.findAll();
    }

    @Override
    public Optional<Pelicula> getPeliculaById(Long id) {
        return peliculaRepository.findById(id);
    }

    @Override
    public Pelicula createPelicula(String titulo, String sinopsis, Integer duracion, String clasificacion, String idioma, String posterUrl) {
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