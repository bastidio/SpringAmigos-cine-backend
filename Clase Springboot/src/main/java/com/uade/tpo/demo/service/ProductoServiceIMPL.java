package com.uade.tpo.demo.service;
import java.math.BigDecimal;
import com.uade.tpo.demo.entity.Categoria;
import com.uade.tpo.demo.entity.ImagenProducto;
import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.exceptions.ImagenProductoNotFoundException;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;
import com.uade.tpo.demo.exceptions.ValidacionException;
import com.uade.tpo.demo.exceptions.CategoriaNotFoundException;
import com.uade.tpo.demo.repository.CategoriaRepository;
import com.uade.tpo.demo.repository.ImagenProductoRepository;
import com.uade.tpo.demo.repository.ProductoRepository;
import com.uade.tpo.demo.repository.ProductoSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    public List<Producto> getProductosInactivos() {
        return productoRepository.findAll(ProductoSpecifications.noEstaActivo());
    }

    @Override
    public Producto reactivarProducto(Long id) throws ProductoNotFoundException {
        // findById sin filtro de activo: justamente queremos alcanzar los inactivos.
        Producto producto = productoRepository.findById(id).orElseThrow(ProductoNotFoundException::new);
        producto.setActivo(true);
        return productoRepository.save(producto);
    }

    @Override
    public Producto createProducto(Long categoriaId, String nombre, String descripcion, BigDecimal precio, Integer stock, BigDecimal descuento, List<String> imagenes)
            throws CategoriaNotFoundException {
        validarDatosProducto(categoriaId, nombre, precio, stock, descuento);

        // orElseThrow() pelado lanza NoSuchElementException, que nadie maneja y
        // sale como 500 con stacktrace. Con la excepcion propia devuelve 404.
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(CategoriaNotFoundException::new);

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
    public Producto updateProducto(Long id, Long categoriaId, String nombre, String descripcion, BigDecimal precio, Integer stock, BigDecimal descuento)
            throws ProductoNotFoundException, CategoriaNotFoundException {
        validarDatosProducto(categoriaId, nombre, precio, stock, descuento);
        Producto producto = productoRepository.findById(id).orElseThrow(ProductoNotFoundException::new);
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(CategoriaNotFoundException::new);

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
        if (nuevoStock == null) {
            throw new ValidacionException(Map.of("stock", "el stock es obligatorio"));
        }
        if (nuevoStock < 0) {
            throw new ValidacionException(Map.of("stock", "el stock no puede ser negativo"));
        }
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
        if (url == null || url.isBlank()) {
            throw new ValidacionException(Map.of("url", "la url de la imagen es obligatoria"));
        }
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

    // Validacion manual del alta/edicion de un producto. Junta todos los errores
    // en un mapa campo -> motivo y, si hay alguno, corta con ValidacionException
    // (GlobalExceptionHandler la traduce a 400).
    private void validarDatosProducto(Long categoriaId, String nombre, BigDecimal precio, Integer stock, BigDecimal descuento) {
        Map<String, String> errores = new LinkedHashMap<>();

        if (categoriaId == null) {
            errores.put("categoriaId", "la categoria es obligatoria");
        }
        if (nombre == null || nombre.isBlank()) {
            errores.put("nombre", "el nombre es obligatorio");
        }
        if (precio == null) {
            errores.put("precio", "el precio es obligatorio");
        } else if (precio.signum() <= 0) {
            errores.put("precio", "el precio debe ser mayor a 0");
        }
        if (stock == null) {
            errores.put("stock", "el stock es obligatorio");
        } else if (stock < 0) {
            errores.put("stock", "el stock no puede ser negativo");
        }
        if (descuento != null
                && (descuento.signum() < 0 || descuento.compareTo(BigDecimal.valueOf(100)) > 0)) {
            errores.put("descuento", "el descuento debe estar entre 0 y 100");
        }

        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }
    }
}
