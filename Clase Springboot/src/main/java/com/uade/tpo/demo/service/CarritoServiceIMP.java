package com.uade.tpo.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uade.tpo.demo.entity.Asiento;
import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.entity.ItemCarrito;
import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.exceptions.AsientoNotFoundException;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;
import com.uade.tpo.demo.exceptions.StockInsuficienteException;
import com.uade.tpo.demo.exceptions.UsuarioNotFoundException;
import com.uade.tpo.demo.repository.AsientoRepository;
import com.uade.tpo.demo.repository.CarritoRepository;
import com.uade.tpo.demo.repository.ItemCarritoRepository;
import com.uade.tpo.demo.repository.ProductoRepository;
import com.uade.tpo.demo.repository.UsuarioRepository;
import java.util.List;

@Service
public class CarritoServiceIMP implements CarritoService {
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
    public Carrito agregarItem(Long usuarioId, Long productoId, Long asientoId, Integer cantidad)
            throws UsuarioNotFoundException, ProductoNotFoundException, AsientoNotFoundException, StockInsuficienteException {

        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);


        ItemCarrito nuevoItem = new ItemCarrito();
        nuevoItem.setCarrito(carrito);
        nuevoItem.setCantidad(cantidad);


        if (productoId != null) {
            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(ProductoNotFoundException::new);

            if (producto.getStock() == null || producto.getStock() < cantidad) {
                throw new StockInsuficienteException();
            }

            nuevoItem.setProducto(producto);
        }

        else if (asientoId != null) {
            Asiento asiento = asientoRepository.findById(asientoId)
                    .orElseThrow(AsientoNotFoundException::new);
            nuevoItem.setAsiento(asiento);
        }


        itemCarritoRepository.save(nuevoItem);


        return carrito;
    }

    @Override
    public Carrito eliminarItem(Long usuarioId, Long itemCarritoId) throws UsuarioNotFoundException {
        itemCarritoRepository.deleteById(itemCarritoId);

        return this.obtenerCarritoPorUsuario(usuarioId);
    }

    @Override
    public Float calcularTotal(Long usuarioId) throws UsuarioNotFoundException {
        
        // construimos
        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);
        
        // enlistas los items del carrito
        List<ItemCarrito> items = itemCarritoRepository.findAllByCarrito(carrito);
        
        Float total = 0f;
        
        // for each para separar
        for (ItemCarrito item : items) {
            Float precioItem = 0f;
            
            //precio del producto
            if (item.getProducto() != null) {
                precioItem = item.getProducto().getPrecio(); 
            } 

            else if (item.getAsiento() != null) {
                //
                if (carrito.getFuncion() != null) {
                    precioItem = carrito.getFuncion().getPrecio_base();
                }
            }
            
            // Al usar Float en todo, multiplicamos directamente
            Float subtotal = precioItem * item.getCantidad();
            
            
            total = total + subtotal;
        }
        
        return total;
    }
    @Override
    public void vaciarCarrito(Long usuarioId) throws UsuarioNotFoundException {
        Carrito carrito = this.obtenerCarritoPorUsuario(usuarioId);
        itemCarritoRepository.deleteAllByCarrito(carrito);
    }
}