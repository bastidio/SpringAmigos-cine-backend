package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Orden;
import com.uade.tpo.demo.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    @PostMapping("/{usuarioId}")
    public ResponseEntity<Orden> procesarCheckout(@PathVariable Long usuarioId) {
        // llamama sin saber que hace el metodo de checkoutService como nos dijo la profe
        Orden ordenConfirmada = checkoutService.procesarCheckout(usuarioId);
        return ResponseEntity.created(URI.create("/api/checkout/" + usuarioId)).body(ordenConfirmada);
    }
}