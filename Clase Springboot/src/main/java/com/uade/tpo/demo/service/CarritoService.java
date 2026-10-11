package com.uade.tpo.demo.service;
import java.math.BigDecimal;
import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.exceptions.AsientoNotFoundException;
import com.uade.tpo.demo.exceptions.CarritoExpiradoException;
import com.uade.tpo.demo.exceptions.ItemCarritoNotFoundException;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;
import com.uade.tpo.demo.exceptions.StockInsuficienteException;
import com.uade.tpo.demo.exceptions.UsuarioNotFoundException;
import com.uade.tpo.demo.exceptions.FuncionNotFoundException;
import com.uade.tpo.demo.exceptions.SeleccionButacaInvalidaException;
import com.uade.tpo.demo.exceptions.ItemCarritoInvalidoException;
import com.uade.tpo.demo.exceptions.CantidadInvalidaException;
import com.uade.tpo.demo.entity.dto.CarritoResponse;

public interface CarritoService {
    Carrito obtenerCarritoPorUsuario(Long usuarioId) throws UsuarioNotFoundException;


    Carrito agregarItem(Long usuarioId, Long productoId, Long asientoId, Long funcionId, Integer cantidad)
            throws UsuarioNotFoundException, ProductoNotFoundException, AsientoNotFoundException,
                   StockInsuficienteException, FuncionNotFoundException, SeleccionButacaInvalidaException,
                   ItemCarritoInvalidoException, CantidadInvalidaException, CarritoExpiradoException;

    Carrito eliminarItem(Long usuarioId, Long itemCarritoId)
            throws UsuarioNotFoundException, ItemCarritoNotFoundException, CarritoExpiradoException;

    Carrito modificarCantidad(Long usuarioId, Long itemCarritoId, Integer nuevaCantidad)
            throws UsuarioNotFoundException, ItemCarritoNotFoundException, StockInsuficienteException,
                   SeleccionButacaInvalidaException, CantidadInvalidaException, CarritoExpiradoException;
    
    // Carrito con sus items y el total ya calculado, listo para el frontend.
    CarritoResponse obtenerCarritoDetallado(Long usuarioId) throws UsuarioNotFoundException;

    BigDecimal calcularTotal(Long usuarioId) throws UsuarioNotFoundException;


    void vaciarCarrito(Long usuarioId) throws UsuarioNotFoundException;

    // Para el scheduler: vacia el carrito solo si sigue vencido antes de "limite" al momento de borrarlo.
    void vaciarSiVencido(Long carritoId, java.time.LocalDateTime limite);
}
