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
    // rollbackFor = Exception.class porque AsientoOcupadoException es checked y sin esto no hacia rollback, dejaba la orden a medias.
    @Transactional(rollbackFor = Exception.class)
    public Orden procesarCheckout(Long usuarioId) throws CarritoVacioException, StockInsuficienteException,
            AsientoOcupadoException, ProductoNotFoundException {

        // 1. buqueda x id -Si no hay fila de carrito lo tratamos como carrito vacio
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(CarritoVacioException::new);

        // los item del carrito
        List<ItemCarrito> items = itemCarritoRepository.findAllByCarrito(carrito);

        if (items.isEmpty()) {
            throw new CarritoVacioException();
        }

        // chequeamos stock y estado de todos los items antes de confirmar 
        for (ItemCarrito item : items) {
            if (item.getProducto() != null) {
                Producto producto = item.getProducto();

                // Puede haber quedado en el carrito de antes de darse de baja.
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

        nuevaOrden.setFecha(java.time.LocalDateTime.now());
        nuevaOrden.setEstado("CONFIRMADA");
        nuevaOrden.setTotal(BigDecimal.ZERO);


        nuevaOrden = ordenRepository.save(nuevaOrden);

        BigDecimal totalCalculado = BigDecimal.ZERO;

        
        for (ItemCarrito item : items) {

            if (item.getProducto() != null) {
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
                    // No deberia pasar: agregarItem siempre asocia funcion si hay butacas. Si pasa, algo esta mal.
                    throw new IllegalStateException("El carrito tiene butacas pero no tiene funcion asociada");
                }

                Entrada entrada = new Entrada();
                entrada.setOrden(nuevaOrden);
                entrada.setFuncion(funcion);
                entrada.setAsiento(item.getAsiento());
                entrada.setPrecio(funcion.getPrecio_base());
                try {
                    // saveAndFlush para que como no se cumple el UNIQUE salte aca y no al cerrar la transaccion.
                    entradaRepository.saveAndFlush(entrada);
                } catch (DataIntegrityViolationException e) {
                    // la butaca ya estaba vendida (choca con unique).
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

        // Se libera la funcion del carrito para poder comprar butacas de otra funcion despues.
        carrito.setFuncion(null);
        carritoRepository.save(carrito);

        return nuevaOrden;
    }
}