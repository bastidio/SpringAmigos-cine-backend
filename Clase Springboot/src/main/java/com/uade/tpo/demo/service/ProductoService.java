package com.uade.tpo.demo.service;
import java.math.BigDecimal;
import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.exceptions.CategoriaNotFoundException;
import com.uade.tpo.demo.exceptions.ImagenProductoNotFoundException;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> getProductos(String nombre, Long categoriaId, BigDecimal precioMin, BigDecimal precioMax);

    // incluirInactivos solo para ADMIN (solicitante null = anonimo); si no, AccessDeniedException.
    List<Producto> getProductos(String nombre, Long categoriaId, BigDecimal precioMin, BigDecimal precioMax,
            boolean incluirInactivos, Usuario solicitante);

    Optional<Producto> getProductoById(Long id);

    // Un ADMIN tambien ve los inactivos (para poder editarlos), el resto solo los activos.
    Optional<Producto> getProductoById(Long id, Usuario solicitante);

    // Solo ADMIN (ver SecurityConfig): productos dados de baja logica.
    List<Producto> getProductosInactivos();

    // Deshace deleteProducto: vuelve a poner activo = true.
    Producto reactivarProducto(Long id) throws ProductoNotFoundException;

    Producto createProducto(Long categoriaId, String nombre, String descripcion, BigDecimal precio, Integer stock, BigDecimal descuento, List<String> imagenes,
            Boolean activo, String posterProducto)
            throws CategoriaNotFoundException;

    Producto updateProducto(Long id, Long categoriaId, String nombre, String descripcion, BigDecimal precio, Integer stock, BigDecimal descuento,
            Boolean activo, String posterProducto)
            throws ProductoNotFoundException, CategoriaNotFoundException;

    Producto actualizarStock(Long id, Integer nuevoStock) throws ProductoNotFoundException;

    // Activa o desactiva (baja logica) en un solo endpoint para el panel admin.
    Producto cambiarEstado(Long id, Boolean activo) throws ProductoNotFoundException;

    void deleteProducto(Long id) throws ProductoNotFoundException;

    Producto agregarImagen(Long productoId, String url) throws ProductoNotFoundException;

    void eliminarImagen(Long productoId, Long imagenId) throws ProductoNotFoundException, ImagenProductoNotFoundException;
}
