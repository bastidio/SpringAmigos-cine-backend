package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.*;
import com.uade.tpo.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
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

    @Override
    @Transactional // boton de emegencia jeje
    public Orden procesarCheckout(Long usuarioId) {
        
        // 1. buqueda x id bbto
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("No se encontró el carrito"));

        // 2. Creacion de la orden bro
        Orden nuevaOrden = new Orden();
        
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        nuevaOrden.setUsuario(usuario);
        
        nuevaOrden.setFecha(new Date(System.currentTimeMillis())); // Fecha actual
        nuevaOrden.setEstado("CONFIRMADA");
        nuevaOrden.setTotal(0f);

        
        nuevaOrden = ordenRepository.save(nuevaOrden);

        // 3.los item del carrito x id el metodo echo x nosostros(mente colmena)
        List<ItemCarrito> items = itemCarritoRepository.findAllByCarrito(carrito);
        float totalCalculado = 0f;

        // 4. for each 
        for (ItemCarrito item : items) {
            
            if (item.getProducto() != null) {
                ItemOrden itemOrden = new ItemOrden();
                itemOrden.setOrden(nuevaOrden);
                itemOrden.setProducto(item.getProducto());
                itemOrden.setCantidad(item.getCantidad());
                itemOrden.setPrecio_unitario(item.getProducto().getPrecio());
                
                itemOrdenRepository.save(itemOrden);
                
                
                totalCalculado += (item.getProducto().getPrecio() * item.getCantidad());
                
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

        // 5. Actualizacion total y guardamiento de esta ya tu sabe
        nuevaOrden.setTotal(totalCalculado);
        ordenRepository.save(nuevaOrden);

        // 6. se borra el carrito para una nueva vida jeje
        itemCarritoRepository.deleteAll(items);

        return nuevaOrden;
    }
}