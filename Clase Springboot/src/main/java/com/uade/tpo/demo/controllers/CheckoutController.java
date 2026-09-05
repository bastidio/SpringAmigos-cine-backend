package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Orden;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.exceptions.CarritoVacioException;
import com.uade.tpo.demo.exceptions.StockInsuficienteException;
import com.uade.tpo.demo.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.demo.exceptions.AsientoOcupadoException;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;

import java.net.URI;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<Orden> procesarCheckout(@AuthenticationPrincipal Usuario usuario)
            throws CarritoVacioException, StockInsuficienteException, AsientoOcupadoException,
                   ProductoNotFoundException {
        Orden ordenConfirmada = checkoutService.procesarCheckout(usuario.getId());
        return ResponseEntity.created(URI.create("/api/checkout")).body(ordenConfirmada);
    }
}
