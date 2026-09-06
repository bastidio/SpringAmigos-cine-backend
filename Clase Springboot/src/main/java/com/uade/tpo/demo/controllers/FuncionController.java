package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Funcion;
import com.uade.tpo.demo.entity.dto.FuncionRequest;
import com.uade.tpo.demo.service.FuncionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/funciones")
public class FuncionController {

    @Autowired
    private FuncionService funcionService;

    @GetMapping
    public ResponseEntity<List<Funcion>> getFunciones() {
        return ResponseEntity.ok(funcionService.getFunciones());
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
}