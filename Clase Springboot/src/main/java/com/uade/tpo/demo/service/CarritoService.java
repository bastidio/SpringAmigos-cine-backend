package com.uade.tpo.demo.service;
import java.math.BigDecimal;
import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.exceptions.AsientoNotFoundException;
import com.uade.tpo.demo.exceptions.ItemCarritoNotFoundException;
import com.uade.tpo.demo.exceptions.ProductoNotFoundException;
import com.uade.tpo.demo.exceptions.StockInsuficienteException;
import com.uade.tpo.demo.exceptions.UsuarioNotFoundException;
import com.uade.tpo.demo.exceptions.FuncionNotFoundException;
import com.uade.tpo.demo.exceptions.SeleccionButacaInvalidaException;

public interface CarritoService {
    Carrito obtenerCarritoPorUsuario(Long usuarioId) throws UsuarioNotFoundException;


    Carrito agregarItem(Long usuarioId, Long productoId, Long asientoId, Long funcionId, Integer cantidad)
            throws UsuarioNotFoundException, ProductoNotFoundException, AsientoNotFoundException,
                   StockInsuficienteException, FuncionNotFoundException, SeleccionButacaInvalidaException;

    Carrito eliminarItem(Long usuarioId, Long itemCarritoId) throws UsuarioNotFoundException, ItemCarritoNotFoundException;

    Carrito modificarCantidad(Long usuarioId, Long itemCarritoId, Integer nuevaCantidad) throws UsuarioNotFoundException, ItemCarritoNotFoundException, StockInsuficienteException;

    BigDecimal calcularTotal(Long usuarioId) throws UsuarioNotFoundException;


    void vaciarCarrito(Long usuarioId) throws UsuarioNotFoundException;
}
