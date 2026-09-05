package com.uade.tpo.demo.service;
import java.math.BigDecimal;
import com.uade.tpo.demo.entity.Categoria;
import com.uade.tpo.demo.entity.ImagenProducto;
import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.exceptions.ImagenProductoNotFoundException;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;
import com.uade.tpo.demo.repository.CategoriaRepository;
import com.uade.tpo.demo.repository.ImagenProductoRepository;
import com.uade.tpo.demo.repository.ProductoRepository;
import com.uade.tpo.demo.repository.ProductoSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceIMPL implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ImagenProductoRepository imagenProductoRepository;

    @Override
    public List<Producto> getProductos(String nombre, Long categoriaId, BigDecimal precioMin, BigDecimal precioMax) {
        Specification<Producto> spec = Specification.where(ProductoSpecifications.estaActivo());

        if (nombre != null && !nombre.isBlank()) {
            spec = spec.and(ProductoSpecifications.nombreContiene(nombre));
        }
        if (categoriaId != null) {
            spec = spec.and(ProductoSpecifications.categoriaEs(categoriaId));
        }
        if (precioMin != null) {
            spec = spec.and(ProductoSpecifications.precioMayorOIgualQue(precioMin));
        }
        if (precioMax != null) {
            spec = spec.and(ProductoSpecifications.precioMenorOIgualQue(precioMax));
        }

        return productoRepository.findAll(spec);
    }

    @Override
    public Optional<Producto> getProductoById(Long id) {
        return productoRepository.findById(id).filter(p -> Boolean.TRUE.equals(p.getActivo()));
    }

    @Override
    public Producto createProducto(Long categoriaId, String nombre, String descripcion, Float precio, Integer stock, Float descuento, List<String> imagenes) {
        Categoria categoria = categoriaRepository.findById(categoriaId).orElseThrow();

        Producto nuevoProducto = new Producto();
        nuevoProducto.setCategoria(categoria);
        nuevoProducto.setNombre(nombre);
        nuevoProducto.setDescripcion(descripcion);
        nuevoProducto.setPrecio(precio);
        nuevoProducto.setStock(stock);
        nuevoProducto.setDescuento(descuento);

        List<ImagenProducto> imagenesProducto = new ArrayList<>();
        if (imagenes != null) {
            for (String url : imagenes) {
                ImagenProducto imagenProducto = new ImagenProducto();
                imagenProducto.setUrl(url);
                imagenProducto.setProducto(nuevoProducto);
                imagenesProducto.add(imagenProducto);
            }
        }
        nuevoProducto.setImagenes(imagenesProducto);

        return productoRepository.save(nuevoProducto);
    }

    @Override
    public Producto updateProducto(Long id, Long categoriaId, String nombre, String descripcion, Float precio, Integer stock, Float descuento) throws ProductoNotFoundException {
        Producto producto = productoRepository.findById(id).orElseThrow(ProductoNotFoundException::new);
        Categoria categoria = categoriaRepository.findById(categoriaId).orElseThrow();

        producto.setCategoria(categoria);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setDescuento(descuento);

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
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Override
    public Producto agregarImagen(Long productoId, String url) throws ProductoNotFoundException {
        Producto producto = productoRepository.findById(productoId).orElseThrow(ProductoNotFoundException::new);

        ImagenProducto nuevaImagen = new ImagenProducto();
        nuevaImagen.setProducto(producto);
        nuevaImagen.setUrl(url);
        imagenProductoRepository.save(nuevaImagen);

        return productoRepository.findById(productoId).orElseThrow(ProductoNotFoundException::new);
    }

    @Override
    public void eliminarImagen(Long productoId, Long imagenId) throws ProductoNotFoundException, ImagenProductoNotFoundException {
        Producto producto = productoRepository.findById(productoId).orElseThrow(ProductoNotFoundException::new);

        ImagenProducto imagen = imagenProductoRepository.findByIdAndProducto(imagenId, producto)
                .orElseThrow(ImagenProductoNotFoundException::new);

        imagenProductoRepository.delete(imagen);
    }
}
