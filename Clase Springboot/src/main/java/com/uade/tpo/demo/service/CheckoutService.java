package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Orden;
import com.uade.tpo.demo.exceptions.CarritoVacioException;
import com.uade.tpo.demo.exceptions.StockInsuficienteException;
import com.uade.tpo.demo.exceptions.AsientoOcupadoException;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;
import com.uade.tpo.demo.exceptions.CarritoExpiradoException;
import com.uade.tpo.demo.exceptions.CodigoDescuentoInvalidoException;

public interface CheckoutService {
    // codigoDescuento opcional (null o vacio = sin descuento): se vuelve a validar y el total se recalcula aca.
    Orden procesarCheckout(Long usuarioId, String codigoDescuento) throws CarritoVacioException, StockInsuficienteException, AsientoOcupadoException, ProductoNotFoundException, CarritoExpiradoException, CodigoDescuentoInvalidoException;
}
