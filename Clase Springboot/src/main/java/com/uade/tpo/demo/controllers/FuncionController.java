package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Funcion;
import com.uade.tpo.demo.entity.dto.FuncionRequest;
import com.uade.tpo.demo.entity.dto.FuncionResponse;
import com.uade.tpo.demo.service.FuncionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.demo.exceptions.FuncionNotFoundException;
import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/funciones")
public class FuncionController {

    @Autowired
    private FuncionService funcionService;

    // GET /funciones?fecha=YYYY-MM-DD -> funciones de ese dia; sin fecha, todas.
    @GetMapping
    public ResponseEntity<List<FuncionResponse>> getFunciones(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(funcionService.getFuncionesResponse(fecha));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcion> getFuncionById(@PathVariable Long id) {
        Optional<Funcion> result = funcionService.getFuncionById(id);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Funcion> createFuncion(@RequestBody FuncionRequest request)
            throws PeliculaNotFoundException, SalaNotFoundException {
        Funcion nuevaFuncion = funcionService.createFuncion(
                request.getPeliculaId(),
                request.getSalaId(),
                request.getHorario(),
                request.getIdioma(),
                request.getFormato(),
                request.getPrecio_base()
        );
        return ResponseEntity.created(URI.create("/funciones/" + nuevaFuncion.getId())).body(nuevaFuncion);
    }

    // Solo ADMIN (PUT sobre CATALOGO en SecurityConfig).
    @PutMapping("/{id}")
    public ResponseEntity<Funcion> updateFuncion(@PathVariable Long id, @RequestBody FuncionRequest request)
            throws FuncionNotFoundException, PeliculaNotFoundException, SalaNotFoundException {
        Funcion funcionActualizada = funcionService.updateFuncion(
                id,
                request.getPeliculaId(),
                request.getSalaId(),
                request.getHorario(),
                request.getIdioma(),
                request.getFormato(),
                request.getPrecio_base()
        );
        return ResponseEntity.ok(funcionActualizada);
    }
}
