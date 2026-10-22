package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Pelicula;
import com.uade.tpo.demo.entity.dto.PeliculaRequest;
import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;
import com.uade.tpo.demo.service.PeliculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/peliculas")
public class PeliculaController {

    @Autowired
    private PeliculaService peliculaService;

    @GetMapping
    public ResponseEntity<List<Pelicula>> getPeliculas() {
        return ResponseEntity.ok(peliculaService.getPeliculas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pelicula> getPeliculaById(@PathVariable Long id) {
        Optional<Pelicula> result = peliculaService.getPeliculaById(id);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Pelicula> createPelicula(@RequestBody PeliculaRequest request) {
        Pelicula nuevaPelicula = peliculaService.createPelicula(
                request.getTitulo(),
                request.getSinopsis(),
                request.getDuracion(),
                request.getClasificacion(),
                request.getIdioma(),
                request.getPoster_url()
        );
        return ResponseEntity.created(URI.create("/peliculas/" + nuevaPelicula.getId())).body(nuevaPelicula);
    }

    // Solo ADMIN (PUT/DELETE sobre CATALOGO en SecurityConfig).
    @PutMapping("/{id}")
    public ResponseEntity<Pelicula> updatePelicula(@PathVariable Long id, @RequestBody PeliculaRequest request)
            throws PeliculaNotFoundException {
        Pelicula peliculaActualizada = peliculaService.updatePelicula(
                id,
                request.getTitulo(),
                request.getSinopsis(),
                request.getDuracion(),
                request.getClasificacion(),
                request.getIdioma(),
                request.getPoster_url()
        );
        return ResponseEntity.ok(peliculaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePelicula(@PathVariable Long id) throws PeliculaNotFoundException {
        peliculaService.deletePelicula(id);
        return ResponseEntity.noContent().build();
    }
}
