package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.CodigoDescuento;
import com.uade.tpo.demo.entity.dto.CodigoDescuentoRequest;
import com.uade.tpo.demo.exceptions.CodigoDescuentoDuplicateException;
import com.uade.tpo.demo.exceptions.CodigoDescuentoNotFoundException;
import com.uade.tpo.demo.service.CodigoDescuentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// Gestion de codigos de descuento. Solo ADMIN (ver SecurityConfig).
@RestController
@RequestMapping("/descuentos")
public class CodigoDescuentoController {

    @Autowired
    private CodigoDescuentoService codigoDescuentoService;

    @GetMapping
    public ResponseEntity<List<CodigoDescuento>> getCodigos() {
        return ResponseEntity.ok(codigoDescuentoService.getCodigos());
    }

    @PostMapping
    public ResponseEntity<CodigoDescuento> createCodigo(@RequestBody CodigoDescuentoRequest request)
            throws CodigoDescuentoDuplicateException {
        CodigoDescuento nuevo = codigoDescuentoService.createCodigo(
                request.getCodigo(), request.getPorcentaje(), request.getFechaVencimiento());
        return ResponseEntity.created(URI.create("/descuentos/" + nuevo.getId())).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CodigoDescuento> updateCodigo(@PathVariable Long id,
            @RequestBody CodigoDescuentoRequest request) throws CodigoDescuentoNotFoundException {
        return ResponseEntity.ok(codigoDescuentoService.updateCodigo(
                id, request.getPorcentaje(), request.getFechaVencimiento()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCodigo(@PathVariable Long id) throws CodigoDescuentoNotFoundException {
        codigoDescuentoService.deleteCodigo(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<CodigoDescuento> reactivarCodigo(@PathVariable Long id)
            throws CodigoDescuentoNotFoundException {
        return ResponseEntity.ok(codigoDescuentoService.reactivarCodigo(id));
    }
}
