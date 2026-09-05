package com.uade.tpo.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.uade.tpo.demo.entity.Asiento;
import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.ItemCarrito;
import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.exceptions.AsientoNotFoundException;
import com.uade.tpo.demo.exceptions.ItemCarritoNotFoundException;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;
import com.uade.tpo.demo.exceptions.StockInsuficienteException;
import com.uade.tpo.demo.exceptions.UsuarioNotFoundException;
import com.uade.tpo.demo.repository.AsientoRepository;
import com.uade.tpo.demo.repository.CarritoRepository;
import com.uade.tpo.demo.repository.ItemCarritoRepository;
import com.uade.tpo.demo.repository.ProductoRepository;
import com.uade.tpo.demo.repository.UsuarioRepository;
import com.uade.tpo.demo.util.PrecioUtils;

import com.uade.tpo.demo.entity.Funcion;
import com.uade.tpo.demo.repository.FuncionRepository;
import com.uade.tpo.demo.exceptions.FuncionNotFoundException;
import com.uade.tpo.demo.exceptions.SeleccionButacaInvalidaException;
import com.uade.tpo.demo.exceptions.AsientoOcupadoException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarritoServiceIMPL implements CarritoService {
   @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private ItemCarritoRepository itemCarritoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private AsientoRepository asientoRepository;
    @Autowired
    private FuncionRepository funcionRepository;

    @Override
    public Carrito obtenerCarritoPorUsuario(Long usuarioId) throws UsuarioNotFoundException {
        Optional<Carrito> carritoExistente = carritoRepository.findByUsuarioId(usuarioId);

        if (carritoExistente.isPresent()) {
            return carritoExistente.get();
        } else {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(UsuarioNotFoundException::new);

            Carrito nuevoCarrito = new Carrito();
            nuevoCarrito.setUsuario(usuario);

            return carritoRepository.save(nuevoCarrito);
        }
    }

    @Override
    @Transactional
    public Carrito agregarItem(Long usuarioId, Long productoId, Long asientoId, Long funcionId, Integer cantidad)
        throws UsuarioNotFoundException, ProductoNotFoundException, AsientoNotFoundException,
                   StockInsuficienteException, FuncionNotFoundException, SeleccionButacaInvalidaException,
                   AsientoOcupadoException {

        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);

        if (productoId != null) {
            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(ProductoNotFoundException::new);

            if (!Boolean.TRUE.equals(producto.getActivo())) {
                throw new ProductoNotFoundException();
            }
            
            Optional<ItemCarrito> itemExistente = itemCarritoRepository.findByCarritoAndProducto(carrito, producto);

            if (itemExistente.isPresent()) {
                ItemCarrito item = itemExistente.get();
                Integer cantidadTotal = item.getCantidad() + cantidad;

                if (producto.getStock() == null || producto.getStock() < cantidadTotal) {
                    throw new StockInsuficienteException();
                }

                item.setCantidad(cantidadTotal);
                itemCarritoRepository.save(item);
            } else {
                if (producto.getStock() == null || producto.getStock() < cantidad) {
                    throw new StockInsuficienteException();
                }

                ItemCarrito nuevoItem = new ItemCarrito();
                nuevoItem.setCarrito(carrito);
                nuevoItem.setCantidad(cantidad);
                nuevoItem.setProducto(producto);

                itemCarritoRepository.save(nuevoItem);
            }
        }

        else if (asientoId != null) {
            if (funcionId == null) {
                throw new SeleccionButacaInvalidaException();
            }

            Asiento asiento = asientoRepository.findById(asientoId)
                    .orElseThrow(AsientoNotFoundException::new);

            Funcion funcion = funcionRepository.findById(funcionId)
                    .orElseThrow(FuncionNotFoundException::new);

            // La butaca tiene que pertenecer a la sala donde se da la funcion.
            if (asiento.getSala() == null || funcion.getSala() == null
                    || !asiento.getSala().getId().equals(funcion.getSala().getId())) {
                throw new SeleccionButacaInvalidaException();
            }

            // Si otro carrito tiene esta butaca reservada y la reserva sigue vigente, no se puede agregar.
            LocalDateTime limite = LocalDateTime.now().minusMinutes(ItemCarrito.MINUTOS_RESERVA);
            if (itemCarritoRepository
                    .findByAsiento_IdAndCarrito_IdNotAndReservadoEnAfter(asientoId, carrito.getId(), limite)
                    .isPresent()) {
                throw new AsientoOcupadoException();
            }

            // El modelo soporta una sola funcion por carrito: si ya hay butacas de otra, se rechaza.
            if (carrito.getFuncion() != null
                    && !carrito.getFuncion().getId().equals(funcion.getId())) {
                throw new SeleccionButacaInvalidaException();
            }

            // ESTO es lo que faltaba: asociar la funcion al carrito.
            // Sin esta linea, el checkout nunca creaba la Entrada (el "agujero negro").
            carrito.setFuncion(funcion);
            carritoRepository.save(carrito);

            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setAsiento(asiento);
            nuevoItem.setReservadoEn(LocalDateTime.now());

            itemCarritoRepository.save(nuevoItem);
        }

        return carrito;
    }

    @Override
    @Transactional
    public Carrito eliminarItem(Long usuarioId, Long itemCarritoId) throws UsuarioNotFoundException, ItemCarritoNotFoundException {
        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);

        ItemCarrito item = itemCarritoRepository.findByIdAndCarrito(itemCarritoId, carrito)
                .orElseThrow(ItemCarritoNotFoundException::new);

        itemCarritoRepository.delete(item);

        return carrito;
    }

    @Override
    @Transactional
    public Carrito modificarCantidad(Long usuarioId, Long itemCarritoId, Integer nuevaCantidad)
            throws UsuarioNotFoundException, ItemCarritoNotFoundException, StockInsuficienteException {
        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);

        ItemCarrito item = itemCarritoRepository.findByIdAndCarrito(itemCarritoId, carrito)
                .orElseThrow(ItemCarritoNotFoundException::new);

        if (item.getProducto() != null) {
            Producto producto = item.getProducto();
            if (producto.getStock() == null || producto.getStock() < nuevaCantidad) {
                throw new StockInsuficienteException();
            }
        }

        item.setCantidad(nuevaCantidad);
        itemCarritoRepository.save(item);

        return carrito;
    }

    @Override
    public BigDecimal calcularTotal(Long usuarioId) throws UsuarioNotFoundException {

        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);
        List<ItemCarrito> items = itemCarritoRepository.findAllByCarrito(carrito);

        BigDecimal total = BigDecimal.ZERO;

        for (ItemCarrito item : items) {
            BigDecimal precioItem = BigDecimal.ZERO;

            if (item.getProducto() != null) {
                precioItem = PrecioUtils.precioConDescuento(item.getProducto().getPrecio(), item.getProducto().getDescuento());
            } else if (item.getAsiento() != null) {
                if (carrito.getFuncion() != null) {
                    precioItem = carrito.getFuncion().getPrecio_base();
                }
            }

            BigDecimal subtotal = precioItem.multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }
    @Override
    @Transactional
    public void vaciarCarrito(Long usuarioId) throws UsuarioNotFoundException {
        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);
        itemCarritoRepository.deleteAllByCarrito(carrito);
    }
}