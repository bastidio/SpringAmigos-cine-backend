package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Asiento;
import com.uade.tpo.demo.entity.dto.AsientoRequest;
import com.uade.tpo.demo.exceptions.AsientoNotFoundException;
import com.uade.tpo.demo.exceptions.AsientoOcupadoException;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;
import com.uade.tpo.demo.service.AsientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<Asiento> getAsientoById(@PathVariable Long id) throws AsientoNotFoundException {
        Asiento asiento = asientoService.getAsientoById(id);
        return ResponseEntity.ok(asiento);
    }

    @PostMapping
    public ResponseEntity<Asiento> createAsiento(@RequestBody AsientoRequest request)
            throws SalaNotFoundException, AsientoOcupadoException {
        Asiento nuevoAsiento = asientoService.createAsiento(
                request.getSalaId(),
                request.getFila(),
                request.getNumero()
        );
        return ResponseEntity.created(URI.create("/asientos/" + nuevoAsiento.getId())).body(nuevoAsiento);
    }
}