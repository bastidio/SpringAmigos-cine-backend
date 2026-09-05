package com.uade.tpo.demo.controllers;
import java.math.BigDecimal;
import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.dto.CantidadRequest;
import com.uade.tpo.demo.entity.dto.ItemCarritoRequest;
import com.uade.tpo.demo.exceptions.AsientoNotFoundException;
import com.uade.tpo.demo.exceptions.ItemCarritoNotFoundException;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;
import com.uade.tpo.demo.exceptions.StockInsuficienteException;
import com.uade.tpo.demo.exceptions.UsuarioNotFoundException;
import com.uade.tpo.demo.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.demo.exceptions.FuncionNotFoundException;
import com.uade.tpo.demo.exceptions.SeleccionButacaInvalidaException;
import com.uade.tpo.demo.exceptions.ItemCarritoInvalidoException;
import com.uade.tpo.demo.exceptions.CantidadInvalidaException;

import java.net.URI;

// El usuarioId ya no viaja en el path: sale del principal autenticado
// (@AuthenticationPrincipal), asi se cierra el IDOR que dejaba operar el
// carrito de cualquier usuario con solo cambiar el id de la URL.
@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping
    public ResponseEntity<Carrito> getCarrito(@AuthenticationPrincipal Usuario usuario) throws UsuarioNotFoundException {
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(usuario.getId());
        if (carrito != null) {
            return ResponseEntity.ok(carrito);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/items")
    public ResponseEntity<Carrito> agregarItem(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody ItemCarritoRequest request)
            throws UsuarioNotFoundException, ProductoNotFoundException, AsientoNotFoundException,
                   StockInsuficienteException, FuncionNotFoundException, SeleccionButacaInvalidaException,
                   ItemCarritoInvalidoException, CantidadInvalidaException {

        Carrito carritoActualizado = carritoService.agregarItem(
                usuario.getId(),
                request.getProductoId(),
                request.getAsientoId(),
                request.getFuncionId(),
                request.getCantidad()
        );

        return ResponseEntity.created(URI.create("/api/carritos")).body(carritoActualizado);
    }

    @DeleteMapping("/items/{itemCarritoId}")
    public ResponseEntity<Carrito> eliminarItem(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long itemCarritoId) throws UsuarioNotFoundException, ItemCarritoNotFoundException {

        Carrito carritoActualizado = carritoService.eliminarItem(usuario.getId(), itemCarritoId);
        return ResponseEntity.ok(carritoActualizado);
    }

    @PutMapping("/items/{itemCarritoId}")
    public ResponseEntity<Carrito> modificarCantidad(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long itemCarritoId,
            @RequestBody CantidadRequest request)
            throws UsuarioNotFoundException, ItemCarritoNotFoundException, StockInsuficienteException,
                   SeleccionButacaInvalidaException, CantidadInvalidaException {

        Carrito carritoActualizado = carritoService.modificarCantidad(usuario.getId(), itemCarritoId, request.getCantidad());
        return ResponseEntity.ok(carritoActualizado);
    }

    @DeleteMapping
    public ResponseEntity<Void> vaciarCarrito(@AuthenticationPrincipal Usuario usuario) throws UsuarioNotFoundException {
        carritoService.vaciarCarrito(usuario.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> calcularTotal(@AuthenticationPrincipal Usuario usuario) throws UsuarioNotFoundException {
        BigDecimal total = carritoService.calcularTotal(usuario.getId());
        return ResponseEntity.ok(total);
    }
}
