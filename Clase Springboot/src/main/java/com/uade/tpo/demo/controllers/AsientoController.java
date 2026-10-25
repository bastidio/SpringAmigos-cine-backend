package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Asiento;
import com.uade.tpo.demo.entity.dto.AsientoRequest;
import com.uade.tpo.demo.service.AsientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.demo.exceptions.AsientoNotFoundException;
import com.uade.tpo.demo.exceptions.SalaConFuncionesException;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/asientos")
public class AsientoController {

    @Autowired
    private AsientoService asientoService;

    @GetMapping
    public ResponseEntity<List<Asiento>> getAsientos() {
        return ResponseEntity.ok(asientoService.getAsientos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asiento> getAsientoById(@PathVariable Long id) {
        Optional<Asiento> result = asientoService.getAsientoById(id);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Asiento> createAsiento(@RequestBody AsientoRequest request)
            throws SalaNotFoundException {
        Asiento nuevoAsiento = asientoService.createAsiento(
                request.getSalaId(),
                request.getFila(),
                request.getNumero()
        );
        return ResponseEntity.created(URI.create("/asientos/" + nuevoAsiento.getId())).body(nuevoAsiento);
    }

    // Solo ADMIN (DELETE sobre CATALOGO en SecurityConfig). 409 si la sala tiene funciones.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsiento(@PathVariable Long id)
            throws AsientoNotFoundException, SalaConFuncionesException {
        asientoService.deleteAsiento(id);
        return ResponseEntity.noContent().build();
    }
}