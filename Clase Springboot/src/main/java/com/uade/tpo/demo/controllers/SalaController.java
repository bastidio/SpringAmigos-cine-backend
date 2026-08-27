package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Sala;
import com.uade.tpo.demo.entity.dto.SalaRequest;
import com.uade.tpo.demo.service.SalaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/salas")
public class SalaController {

    @Autowired
    private SalaService salaService; // Inyección del cerebro (Service)

    // 1. GET: Traer todas las salas
    @GetMapping
    public ResponseEntity<List<Sala>> getSalas() {
        return ResponseEntity.ok(salaService.getSalas());
    }

    // 2. GET por ID: Buscar una sala específica
    @GetMapping("/{id}")
    public ResponseEntity<Sala> getSalaById(@PathVariable Long id) {
        Optional<Sala> result = salaService.getSalaById(id);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.noContent().build();
    }

    // 3. POST: Crear una sala nueva
    @PostMapping
    public ResponseEntity<Sala> createSala(@RequestBody SalaRequest salaRequest) {
        Sala nuevaSala = salaService.createSala(salaRequest.getNombre(), salaRequest.getCapacidad());
        // Devuelve 201 Created y avisa en qué URL quedó guardada la nueva sala
        return ResponseEntity.created(URI.create("/salas/" + nuevaSala.getId())).body(nuevaSala);
    }
}