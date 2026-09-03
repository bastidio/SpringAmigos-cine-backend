package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Categoria;
import com.uade.tpo.demo.entity.dto.CategoriaRequest;
import com.uade.tpo.demo.exceptions.CategoriaDuplicateException;
import com.uade.tpo.demo.exceptions.CategoriaNotFoundException;
import com.uade.tpo.demo.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<Categoria> getCategoriaById(@PathVariable Long id) throws CategoriaNotFoundException {
        Categoria categoria = categoriaService.getCategoriaById(id);
        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    public ResponseEntity<Categoria> createCategoria(@RequestBody CategoriaRequest request) throws CategoriaDuplicateException {
        Categoria nuevaCategoria = categoriaService.createCategoria(request.getNombre());
        return ResponseEntity.created(URI.create("/categorias/" + nuevaCategoria.getId())).body(nuevaCategoria);
    }
}
