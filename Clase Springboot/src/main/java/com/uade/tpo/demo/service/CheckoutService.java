package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Orden;

public interface CheckoutService {
    Orden procesarCheckout(Long usuarioId);
}