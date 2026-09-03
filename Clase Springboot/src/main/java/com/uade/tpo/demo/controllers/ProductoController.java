package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.entity.dto.ProductoRequest;
import com.uade.tpo.demo.entity.dto.StockRequest;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;
import com.uade.tpo.demo.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> getProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Float precioMin,
            @RequestParam(required = false) Float precioMax) {
        return ResponseEntity.ok(productoService.getProductos(nombre, categoriaId, precioMin, precioMax));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) throws ProductoNotFoundException {
        Producto producto = productoService.getProductoById(id);
        return ResponseEntity.ok(producto);
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

    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @RequestBody ProductoRequest request)
            throws ProductoNotFoundException {
        Producto productoActualizado = productoService.updateProducto(
                id,
                request.getCategoriaId(),
                request.getNombre(),
                request.getDescripcion(),
                request.getPrecio(),
                request.getStock(),
                request.getDescuento(),
                request.getImagen_url()
        );
        return ResponseEntity.ok(productoActualizado);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Producto> actualizarStock(@PathVariable Long id, @RequestBody StockRequest request) throws ProductoNotFoundException {
        Producto productoActualizado = productoService.actualizarStock(id, request.getStock());
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) throws ProductoNotFoundException {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }
}