package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.entity.dto.ProductoRequest;
import com.uade.tpo.demo.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> getProductos() {
        return ResponseEntity.ok(productoService.getProductos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        Optional<Producto> result = productoService.getProductoById(id);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody ProductoRequest request) {
        Producto nuevoProducto = productoService.createProducto(
                request.getCategoriaId(),
                request.getNombre(),
                request.getDescripcion(),
                request.getPrecio(),
                request.getStock(),
                request.getDescuento(),
                request.getImagen_url()
        );
        return ResponseEntity.created(URI.create("/productos/" + nuevoProducto.getId())).body(nuevoProducto);
    }
}