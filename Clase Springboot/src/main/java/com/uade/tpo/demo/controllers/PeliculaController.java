package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Pelicula;
import com.uade.tpo.demo.entity.dto.PeliculaRequest;
import com.uade.tpo.demo.exceptions.PeliculaDuplicateException;
import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;
import com.uade.tpo.demo.service.PeliculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<Pelicula> getPeliculaById(@PathVariable Long id) throws PeliculaNotFoundException {
        Pelicula pelicula = peliculaService.getPeliculaById(id);
        return ResponseEntity.ok(pelicula);
    }

    // POST: Agregar una nueva película a la cartelera
    @PostMapping
    public ResponseEntity<Pelicula> createPelicula(@RequestBody PeliculaRequest request) throws PeliculaDuplicateException {
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