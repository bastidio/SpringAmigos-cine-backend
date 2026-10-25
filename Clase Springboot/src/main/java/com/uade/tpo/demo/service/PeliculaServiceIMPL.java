package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Pelicula;
import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;
import com.uade.tpo.demo.exceptions.ValidacionException;
import com.uade.tpo.demo.repository.FuncionRepository;
import com.uade.tpo.demo.repository.PeliculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PeliculaServiceIMPL implements PeliculaService {

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private FuncionRepository funcionRepository;

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

    @Override
    public Pelicula updatePelicula(Long id, String titulo, String sinopsis, Integer duracion, String clasificacion, String idioma, String posterUrl)
            throws PeliculaNotFoundException {
        Map<String, String> errores = new LinkedHashMap<>();
        if (titulo == null || titulo.isBlank()) {
            errores.put("titulo", "el titulo es obligatorio");
        }
        if (duracion != null && duracion <= 0) {
            errores.put("duracion", "la duracion debe ser mayor a 0");
        }
        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }

        Pelicula pelicula = peliculaRepository.findById(id).orElseThrow(PeliculaNotFoundException::new);
        pelicula.setTitulo(titulo);
        pelicula.setSinopsis(sinopsis);
        pelicula.setDuracion(duracion);
        pelicula.setClasificacion(clasificacion);
        pelicula.setIdioma(idioma);
        pelicula.setPoster_url(posterUrl);

        return peliculaRepository.save(pelicula);
    }

    @Override
    public void deletePelicula(Long id) throws PeliculaNotFoundException {
        Pelicula pelicula = peliculaRepository.findById(id).orElseThrow(PeliculaNotFoundException::new);
        // Chequeo explicito: si no, la FK de funcion tira DataIntegrityViolation y sale como 500.
        if (funcionRepository.existsByPeliculaId(id)) {
            throw new IllegalStateException(
                    "No se puede eliminar la pelicula porque tiene funciones asignadas");
        }
        peliculaRepository.delete(pelicula);
    }
}
