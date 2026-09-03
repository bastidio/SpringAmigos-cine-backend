package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.exceptions.AsientoNotFoundException;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;
import com.uade.tpo.demo.exceptions.StockInsuficienteException;
import com.uade.tpo.demo.exceptions.UsuarioNotFoundException;

public interface CarritoService {
    Carrito obtenerCarritoPorUsuario(Long usuarioId) throws UsuarioNotFoundException;


    Carrito agregarItem(Long usuarioId, Long productoId, Long asientoId, Integer cantidad)
            throws UsuarioNotFoundException, ProductoNotFoundException, AsientoNotFoundException, StockInsuficienteException;


    Carrito eliminarItem(Long usuarioId, Long itemCarritoId) throws UsuarioNotFoundException;

    Float calcularTotal(Long usuarioId) throws UsuarioNotFoundException;


    void vaciarCarrito(Long usuarioId) throws UsuarioNotFoundException;
}
