package com.uade.tpo.demo.controllers;
import java.math.BigDecimal;
import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.entity.dto.ImagenProductoRequest;
import com.uade.tpo.demo.entity.dto.ProductoRequest;
import com.uade.tpo.demo.entity.dto.StockRequest;
import com.uade.tpo.demo.exceptions.ImagenProductoNotFoundException;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;
import com.uade.tpo.demo.service.ProductoService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<Producto>> getProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax) {
        return ResponseEntity.ok(productoService.getProductos(nombre, categoriaId, precioMin, precioMax));
    }

    // Solo ADMIN (ver SecurityConfig). Declarado antes de /{id} por claridad;
    // Spring MVC ya prioriza la ruta literal sobre la variable igual.
    @GetMapping("/inactivos")
    public ResponseEntity<List<Producto>> getProductosInactivos() {
        return ResponseEntity.ok(productoService.getProductosInactivos());
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
    public ResponseEntity<Producto> createProducto(@Valid @RequestBody ProductoRequest request) {
        Producto nuevoProducto = productoService.createProducto(
                request.getCategoriaId(),
                request.getNombre(),
                request.getDescripcion(),
                request.getPrecio(),
                request.getStock(),
                request.getDescuento(),
                request.getImagenes()
        );
        return ResponseEntity.created(URI.create("/productos/" + nuevoProducto.getId())).body(nuevoProducto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequest request)
            throws ProductoNotFoundException {
        Producto productoActualizado = productoService.updateProducto(
                id,
                request.getCategoriaId(),
                request.getNombre(),
                request.getDescripcion(),
                request.getPrecio(),
                request.getStock(),
                request.getDescuento()
        );
        return ResponseEntity.ok(productoActualizado);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Producto> actualizarStock(@PathVariable Long id, @Valid @RequestBody StockRequest request) throws ProductoNotFoundException {
        Producto productoActualizado = productoService.actualizarStock(id, request.getStock());
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) throws ProductoNotFoundException {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }

    // Deshace la baja logica de deleteProducto. Solo ADMIN (PATCH sobre CATALOGO
    // en SecurityConfig).
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Producto> reactivarProducto(@PathVariable Long id) throws ProductoNotFoundException {
        return ResponseEntity.ok(productoService.reactivarProducto(id));
    }

    @PostMapping("/{id}/imagenes")
    public ResponseEntity<Producto> agregarImagen(@PathVariable Long id, @Valid @RequestBody ImagenProductoRequest request) throws ProductoNotFoundException {
        Producto productoActualizado = productoService.agregarImagen(id, request.getUrl());
        return ResponseEntity.created(URI.create("/productos/" + id)).body(productoActualizado);
    }

    @DeleteMapping("/{id}/imagenes/{imagenId}")
    public ResponseEntity<Void> eliminarImagen(@PathVariable Long id, @PathVariable Long imagenId) throws ProductoNotFoundException, ImagenProductoNotFoundException {
        productoService.eliminarImagen(id, imagenId);
        return ResponseEntity.noContent().build();
    }
}