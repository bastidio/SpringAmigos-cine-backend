package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> getProductos();
    Optional<Producto> getProductoById(Long id);
    Producto createProducto(Long categoriaId, String nombre, String descripcion, Float precio, Integer stock, Float descuento, String imagenUrl);
}