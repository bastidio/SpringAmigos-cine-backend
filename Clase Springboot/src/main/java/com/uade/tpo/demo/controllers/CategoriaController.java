package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Categoria;
import com.uade.tpo.demo.entity.dto.CategoriaRequest;
import com.uade.tpo.demo.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<Categoria>> getCategorias() {
        return ResponseEntity.ok(categoriaService.getCategorias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getCategoriaById(@PathVariable Long id) {
        Optional<Categoria> result = categoriaService.getCategoriaById(id);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Categoria> createCategoria(@RequestBody CategoriaRequest request) {
        Categoria nuevaCategoria = categoriaService.createCategoria(request.getNombre());
        return ResponseEntity.created(URI.create("/categorias/" + nuevaCategoria.getId())).body(nuevaCategoria);
    }
}
