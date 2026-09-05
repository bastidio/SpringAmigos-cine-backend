package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Orden;
import com.uade.tpo.demo.exceptions.CarritoVacioException;
import com.uade.tpo.demo.exceptions.StockInsuficienteException;

import com.uade.tpo.demo.exceptions.AsientoOcupadoException;
import com.uade.tpo.demo.exceptions.ReservaVencidaException;

public interface CheckoutService {
    Orden procesarCheckout(Long usuarioId) throws CarritoVacioException, StockInsuficienteException, AsientoOcupadoException, ReservaVencidaException;
}