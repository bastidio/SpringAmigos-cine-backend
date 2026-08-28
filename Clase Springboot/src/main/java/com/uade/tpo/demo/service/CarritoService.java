package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Carrito;

public interface CarritoService {
    Carrito obtenerCarritoPorUsuario(Long usuarioId);
    
   
    Carrito agregarItem(Long usuarioId, Long productoId, Long asientoId, Integer cantidad);
    
    
    Carrito eliminarItem(Long usuarioId, Long itemCarritoId);
    
    Float calcularTotal(Long usuarioId);
    
    
    void vaciarCarrito(Long usuarioId);
}
