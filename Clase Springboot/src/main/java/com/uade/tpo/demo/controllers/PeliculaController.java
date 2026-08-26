package com.uade.tpo.demo.controllers;

import com.uade.tpo.marketplace.entity.Pelicula;
import com.uade.tpo.marketplace.entity.dto.PeliculaRequest;
import com.uade.tpo.marketplace.service.PeliculaService;
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

    // GET: Traer toda la cartelera
    @GetMapping
    public ResponseEntity<List<Pelicula>> getPeliculas() {
        return ResponseEntity.ok(peliculaService.getPeliculas());
    }

    // GET: Buscar una peli específica
    @GetMapping("/{id}")
    public ResponseEntity<Pelicula> getPeliculaById(@PathVariable Long id) {
        Optional<Pelicula> result = peliculaService.getPeliculaById(id);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.noContent().build();
    }

    // POST: Agregar una nueva película a la cartelera
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
}