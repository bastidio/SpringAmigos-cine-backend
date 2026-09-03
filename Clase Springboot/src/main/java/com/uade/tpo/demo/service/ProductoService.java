package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> getProductos(String nombre, Long categoriaId, Float precioMin, Float precioMax);
    Optional<Producto> getProductoById(Long id);

    Producto createProducto(Long categoriaId, String nombre, String descripcion, Float precio, Integer stock, Float descuento, String imagenUrl);

    Producto updateProducto(Long id, Long categoriaId, String nombre, String descripcion, Float precio, Integer stock, Float descuento, String imagenUrl) throws ProductoNotFoundException;

    Producto actualizarStock(Long id, Integer nuevoStock) throws ProductoNotFoundException;

    void deleteProducto(Long id) throws ProductoNotFoundException;
}
