package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Orden;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.exceptions.CarritoVacioException;
import com.uade.tpo.demo.exceptions.StockInsuficienteException;
import com.uade.tpo.demo.entity.dto.CheckoutRequest;
import com.uade.tpo.demo.entity.dto.DescuentoValidacionResponse;
import com.uade.tpo.demo.exceptions.CodigoDescuentoInvalidoException;
import com.uade.tpo.demo.exceptions.UsuarioNotFoundException;
import com.uade.tpo.demo.service.CarritoService;
import com.uade.tpo.demo.service.CheckoutService;
import com.uade.tpo.demo.service.CodigoDescuentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.demo.exceptions.AsientoOcupadoException;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;
import com.uade.tpo.demo.exceptions.CarritoExpiradoException;

import java.net.URI;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private CodigoDescuentoService codigoDescuentoService;

    @Autowired
    private CarritoService carritoService;

    // Body opcional: solo el codigo. El porcentaje y el total los recalcula el service.
    @PostMapping
    public ResponseEntity<Orden> procesarCheckout(@AuthenticationPrincipal Usuario usuario,
            @RequestBody(required = false) CheckoutRequest request)
            throws CarritoVacioException, StockInsuficienteException, AsientoOcupadoException,
                   ProductoNotFoundException, CarritoExpiradoException, CodigoDescuentoInvalidoException {
        String codigo = request != null ? request.getCodigoDescuento() : null;
        Orden ordenConfirmada = checkoutService.procesarCheckout(usuario.getId(), codigo);
        return ResponseEntity.created(URI.create("/api/checkout")).body(ordenConfirmada);
    }

    // Muestra el descuento antes de pagar, calculado sobre el carrito actual del usuario. No modifica nada.
    @GetMapping("/descuento")
    public ResponseEntity<DescuentoValidacionResponse> validarDescuento(@RequestParam String codigo,
            @AuthenticationPrincipal Usuario usuario) throws UsuarioNotFoundException {
        return ResponseEntity.ok(
                codigoDescuentoService.validar(codigo, carritoService.calcularTotal(usuario.getId())));
    }
}
