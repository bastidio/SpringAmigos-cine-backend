package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.*;
import com.uade.tpo.demo.exceptions.CarritoVacioException;
import com.uade.tpo.demo.exceptions.StockInsuficienteException;
import com.uade.tpo.demo.repository.*;
import com.uade.tpo.demo.util.PrecioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional // boton de emegencia
    public Orden procesarCheckout(Long usuarioId) throws CarritoVacioException, StockInsuficienteException {

        // 1. buqueda x id
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("No se encontró el carrito"));

        // 1.1. los item del carrito x id el metodo echo x nosostros(mente colmena)
        List<ItemCarrito> items = itemCarritoRepository.findAllByCarrito(carrito);

        if (items.isEmpty()) {
            throw new CarritoVacioException();
        }

        // 1.2. validamos el stock de todos los items de producto ANTES de confirmar nada
        for (ItemCarrito item : items) {
            if (item.getProducto() != null) {
                Producto producto = item.getProducto();
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
        nuevaOrden.setTotal(0f);


        nuevaOrden = ordenRepository.save(nuevaOrden);

        float totalCalculado = 0f;

        
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

                Float precioConDescuento = PrecioUtils.precioConDescuento(item.getProducto().getPrecio(), item.getProducto().getDescuento());


                itemOrden.setPrecio_unitario(precioConDescuento);;
                itemOrdenRepository.save(itemOrden);
                totalCalculado += (precioConDescuento * item.getCantidad());
                
            } else if (item.getAsiento() != null && carrito.getFuncion() != null) {
                Entrada entrada = new Entrada();
                entrada.setOrden_id(nuevaOrden); 
                entrada.setFuncion_id(carrito.getFuncion()); 
                entrada.setAsiento_id(item.getAsiento()); 
                entrada.setPrecio(carrito.getFuncion().getPrecio_base());
                
                entradaRepository.save(entrada);
                
                
                totalCalculado += carrito.getFuncion().getPrecio_base();
            }
        }

        // 5. Actualizacion total
        nuevaOrden.setTotal(totalCalculado);
        ordenRepository.save(nuevaOrden);

        // 6. se borra el los items del carrito
        itemCarritoRepository.deleteAll(items);

        return nuevaOrden;
    }
}