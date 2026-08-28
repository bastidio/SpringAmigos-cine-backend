package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.dto.ItemCarritoRequest;
import com.uade.tpo.demo.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<Carrito> getCarritoPorUsuario(@PathVariable Long usuarioId) {
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(usuarioId);
        if (carrito != null) {
            return ResponseEntity.ok(carrito);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{usuarioId}/items")
    public ResponseEntity<Carrito> agregarItem(
            @PathVariable Long usuarioId,
            @RequestBody ItemCarritoRequest request) {
        
        Carrito carritoActualizado = carritoService.agregarItem(
                usuarioId,
                request.getProductoId(), 
                request.getAsientoId(),
                request.getCantidad()
        );
        
        // Retornamos 201 Created con la URI del carrito, mismo estilo que AsientoController
        return ResponseEntity.created(URI.create("/api/carritos/" + usuarioId)).body(carritoActualizado);
    }

    @DeleteMapping("/{usuarioId}/items/{itemCarritoId}")
    public ResponseEntity<Carrito> eliminarItem(
            @PathVariable Long usuarioId, 
            @PathVariable Long itemCarritoId) {
        
        Carrito carritoActualizado = carritoService.eliminarItem(usuarioId, itemCarritoId);
        return ResponseEntity.ok(carritoActualizado);
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> vaciarCarrito(@PathVariable Long usuarioId) {
        carritoService.vaciarCarrito(usuarioId);
        // Devolvemos 204 No Content indicando que la operación fue exitosa pero no hay body
        return ResponseEntity.noContent().build(); 
    }

    @GetMapping("/{usuarioId}/total")
    public ResponseEntity<Float> calcularTotal(@PathVariable Long usuarioId) {
        Float total = carritoService.calcularTotal(usuarioId);
        return ResponseEntity.ok(total);
    }
}