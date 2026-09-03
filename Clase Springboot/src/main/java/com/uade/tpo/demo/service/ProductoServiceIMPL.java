package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Categoria;
import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;
import com.uade.tpo.demo.repository.CategoriaRepository;
import com.uade.tpo.demo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceIMPL implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public List<Producto> getProductos() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> getProductoById(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    public Producto createProducto(Long categoriaId, String nombre, String descripcion, Float precio, Integer stock, Float descuento, String imagenUrl) {
        Categoria categoria = categoriaRepository.findById(categoriaId).orElseThrow();

        Producto nuevoProducto = new Producto();
        nuevoProducto.setCategoria(categoria);
        nuevoProducto.setNombre(nombre);
        nuevoProducto.setDescripcion(descripcion);
        nuevoProducto.setPrecio(precio);
        nuevoProducto.setStock(stock);
        nuevoProducto.setDescuento(descuento);
        nuevoProducto.setImagen_url(imagenUrl);

        return productoRepository.save(nuevoProducto);
    }

    @Override
    public Producto updateProducto(Long id, Long categoriaId, String nombre, String descripcion, Float precio, Integer stock, Float descuento, String imagenUrl) throws ProductoNotFoundException {
        Producto producto = productoRepository.findById(id).orElseThrow(ProductoNotFoundException::new);
        Categoria categoria = categoriaRepository.findById(categoriaId).orElseThrow();

        producto.setCategoria(categoria);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setDescuento(descuento);
        producto.setImagen_url(imagenUrl);

        return productoRepository.save(producto);
    }

    @Override
    public Producto actualizarStock(Long id, Integer nuevoStock) throws ProductoNotFoundException {
        Producto producto = productoRepository.findById(id).orElseThrow(ProductoNotFoundException::new);
        producto.setStock(nuevoStock);
        return productoRepository.save(producto);
    }

    @Override
    public void deleteProducto(Long id) throws ProductoNotFoundException {
        Producto producto = productoRepository.findById(id).orElseThrow(ProductoNotFoundException::new);
        productoRepository.delete(producto);
    }
}
