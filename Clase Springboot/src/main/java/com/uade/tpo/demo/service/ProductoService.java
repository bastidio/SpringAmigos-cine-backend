package com.uade.tpo.demo.service;
import java.math.BigDecimal;
import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.exceptions.ImagenProductoNotFoundException;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> getProductos(String nombre, Long categoriaId, BigDecimal precioMin, BigDecimal precioMax);
    Optional<Producto> getProductoById(Long id);

    Producto createProducto(Long categoriaId, String nombre, String descripcion, BigDecimal precio, Integer stock, BigDecimal descuento, List<String> imagenes);

    Producto updateProducto(Long id, Long categoriaId, String nombre, String descripcion, BigDecimal precio, Integer stock, BigDecimal descuento) throws ProductoNotFoundException;

    Producto actualizarStock(Long id, Integer nuevoStock) throws ProductoNotFoundException;

    void deleteProducto(Long id) throws ProductoNotFoundException;

    Producto agregarImagen(Long productoId, String url) throws ProductoNotFoundException;

    void eliminarImagen(Long productoId, Long imagenId) throws ProductoNotFoundException, ImagenProductoNotFoundException;
}
