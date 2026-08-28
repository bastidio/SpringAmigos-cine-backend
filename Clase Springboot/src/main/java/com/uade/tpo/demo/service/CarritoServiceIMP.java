package com.uade.tpo.demo.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.entity.Asiento;
import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.ItemCarrito;
import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.repository.CarritoRepository;
import com.uade.tpo.demo.repository.ItemCarritoRepository;
import java.util.List;

@Service
public class CarritoServiceIMP implements CarritoService {
   
    private CarritoRepository carritoRepository;
    
    private ItemCarritoRepository itemCarritoRepository;

    @Override
    public Carrito obtenerCarritoPorUsuario(Long usuarioId) {
        // Usamos el nuevo método findByUsuarioId
        Optional<Carrito> carritoExistente = carritoRepository.findByUsuarioId(usuarioId);

        if (carritoExistente.isPresent()) {
            return carritoExistente.get();
        } else {
            Carrito nuevoCarrito = new Carrito();
            
            Usuario usuario = new Usuario();
            usuario.setId(usuarioId);
            
            nuevoCarrito.setUsuario(usuario); 
            
            return carritoRepository.save(nuevoCarrito);
        }
    }

    @Override
    public Carrito agregarItem(Long usuarioId, Long productoId, Long asientoId, Integer cantidad) {
        
        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);

        
        ItemCarrito nuevoItem = new ItemCarrito();
        nuevoItem.setCarrito(carrito);
        nuevoItem.setCantidad(cantidad);

       
        if (productoId != null) {
            Producto producto = new Producto();
            producto.setId(productoId);
            nuevoItem.setProducto(producto);
        } 
       
        else if (asientoId != null) {
            Asiento asiento = new Asiento();
            asiento.setId(asientoId);
            nuevoItem.setAsiento(asiento);
        }

   
        itemCarritoRepository.save(nuevoItem);

      
        return carrito;
    }

    @Override
    public Carrito eliminarItem(Long usuarioId, Long itemCarritoId) {
        itemCarritoRepository.deleteById(itemCarritoId);
        
        return this.obtenerCarritoPorUsuario(usuarioId);
    }

    @Override
    public Float calcularTotal(Long usuarioId) {
        
        // 1. Traemos el carrito del usuario
        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);
        
        // 2. Buscamos todos los ítems que tenga adentro
        List<ItemCarrito> items = itemCarritoRepository.findAllByCarrito(carrito);
        
        Float total = 0f;
        
        // 3. Iteramos sobre cada ítem para sumar los subtotales
        for (ItemCarrito item : items) {
            Float precioItem = 0f;
            
            // Evaluamos de qué tipo de consumible/entrada estamos hablando
            if (item.getProducto() != null) {
                precioItem = item.getProducto().getPrecio(); 
            } 

            else if (item.getAsiento() != null) {
                // Basándonos en tu diagrama, sacamos el precio base de la función
                if (carrito.getFuncion() != null) {
                    precioItem = carrito.getFuncion().getPrecio_base();
                }
            }
            
            // Al usar Float en todo, multiplicamos directamente
            Float subtotal = precioItem * item.getCantidad();
            
            // Lo sumamos al acumulador total
            total = total + subtotal;
        }
        
        return total;
    }
    @Override
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);
        itemCarritoRepository.deleteAllByCarrito(carrito);
    }
}