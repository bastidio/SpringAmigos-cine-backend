package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.*;
import com.uade.tpo.demo.exceptions.CarritoVacioException;
import com.uade.tpo.demo.exceptions.StockInsuficienteException;
import com.uade.tpo.demo.repository.*;
import com.uade.tpo.demo.util.PrecioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import com.uade.tpo.demo.exceptions.AsientoOcupadoException;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CheckoutServiceIMPL implements CheckoutService {

    // Inyectamos los repositorios con Autowired respetando tu formato
    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private ItemOrdenRepository itemOrdenRepository;

    @Autowired
    private EntradaRepository entradaRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    // rollbackFor = Exception.class: AsientoOcupadoException es checked y puede
    // lanzarse despues de haber creado la orden, descontado stock y guardado
    // otras entradas dentro del mismo loop. Sin esto, @Transactional solo
    // revierte ante RuntimeException y esa excepcion dejaba una orden a medias.
    @Transactional(rollbackFor = Exception.class)
    public Orden procesarCheckout(Long usuarioId) throws CarritoVacioException, StockInsuficienteException,
            AsientoOcupadoException, ProductoNotFoundException {

        // 1. buqueda x id
        // Si el usuario nunca agrego nada, no hay fila de carrito. Para el
        // cliente eso es lo mismo que un carrito vacio: 400 con mensaje claro,
        // no un RuntimeException que sale como 500 con stacktrace.
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(CarritoVacioException::new);

        // 1.1. los item del carrito x id el metodo echo x nosostros(mente colmena)
        List<ItemCarrito> items = itemCarritoRepository.findAllByCarrito(carrito);

        if (items.isEmpty()) {
            throw new CarritoVacioException();
        }

        // 1.2. validamos el stock de todos los items de producto ANTES de confirmar nada
        // 1.2. validamos stock y estado de todos los items de producto ANTES de confirmar nada
        for (ItemCarrito item : items) {
            if (item.getProducto() != null) {
                Producto producto = item.getProducto();

                // Un producto dado de baja no se vende, aunque haya quedado en un
                // carrito viejo de antes de la baja (agregarItem ya lo valida al entrar).
                if (!Boolean.TRUE.equals(producto.getActivo())) {
                    throw new ProductoNotFoundException();
                }

                if (producto.getStock() == null || producto.getStock() < item.getCantidad()) {
                    throw new StockInsuficienteException();
                }
            }
        }

        // 2. Creacion de la orden
        Orden nuevaOrden = new Orden();

        nuevaOrden.setUsuario(carrito.getUsuario());

        nuevaOrden.setFecha(java.time.LocalDateTime.now()); // Fecha actual
        nuevaOrden.setEstado("CONFIRMADA");
        nuevaOrden.setTotal(BigDecimal.ZERO);


        nuevaOrden = ordenRepository.save(nuevaOrden);

        BigDecimal totalCalculado = BigDecimal.ZERO;

        
        for (ItemCarrito item : items) {

            if (item.getProducto() != null) {
                // Descontamos el stock del producto real al confirmar
                Producto producto = item.getProducto();
                producto.setStock(producto.getStock() - item.getCantidad());
                productoRepository.save(producto);

                ItemOrden itemOrden = new ItemOrden();
                itemOrden.setOrden(nuevaOrden);
                itemOrden.setProducto(item.getProducto());
                itemOrden.setCantidad(item.getCantidad());
                
                
                itemOrdenRepository.save(itemOrden);

                BigDecimal precioConDescuento = PrecioUtils.precioConDescuento(item.getProducto().getPrecio(), item.getProducto().getDescuento());

                itemOrden.setPrecio_unitario(precioConDescuento);
                itemOrdenRepository.save(itemOrden);
                totalCalculado = totalCalculado.add(precioConDescuento.multiply(BigDecimal.valueOf(item.getCantidad())));
                
            } else if (item.getAsiento() != null) {
                Funcion funcion = carrito.getFuncion();
                if (funcion == null) {
                    // Invariante: agregarItem garantiza que el carrito tenga funcion si hay butacas.
                    // Si llegamos aca con funcion nula, algo esta corrupto: fallamos y el @Transactional revierte todo.
                    throw new IllegalStateException("El carrito tiene butacas pero no tiene funcion asociada");
                }

                Entrada entrada = new Entrada();
                entrada.setOrden(nuevaOrden);
                entrada.setFuncion(funcion);
                entrada.setAsiento(item.getAsiento());
                entrada.setPrecio(funcion.getPrecio_base());
                try {
                    // saveAndFlush fuerza el INSERT ahora, para que la violacion del UNIQUE
                    // salte aca (y no al cerrar la transaccion) y la podamos traducir a un error limpio.
                    entradaRepository.saveAndFlush(entrada);
                } catch (DataIntegrityViolationException e) {
                    // La butaca ya fue vendida para esta funcion (choca contra el UNIQUE).
                    throw new AsientoOcupadoException();
                }

                totalCalculado = totalCalculado.add(funcion.getPrecio_base());

            } else {
                // Ni producto ni asiento: item corrupto. No lo ignoramos en silencio.
                throw new IllegalStateException("Item de carrito invalido: no tiene producto ni asiento");
            }
        }

        // 5. Actualizacion total
        nuevaOrden.setTotal(totalCalculado.setScale(2, RoundingMode.HALF_UP));
        ordenRepository.save(nuevaOrden);

        // 6. se borra el los items del carrito
        itemCarritoRepository.deleteAll(items);

        // El carrito vuelve a quedar libre: sin items y sin funcion asociada,
        // para que el usuario pueda comprar butacas de otra funcion despues.
        carrito.setFuncion(null);
        carritoRepository.save(carrito);

        return nuevaOrden;
    }
}