package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.*;
import com.uade.tpo.demo.entity.dto.ItemOrdenRequest;
import com.uade.tpo.demo.entity.dto.OrdenRequest;
import com.uade.tpo.demo.repository.ItemOrdenRepository;
import com.uade.tpo.demo.repository.OrdenRepository;
import com.uade.tpo.demo.repository.ProductoRepository;
import com.uade.tpo.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdenServiceIMPL implements OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private ItemOrdenRepository itemOrdenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    @Transactional
    public Orden createOrden(OrdenRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getUsuarioId()));

        // 1. Guardar la orden inicial
        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setFecha(LocalDateTime.now());
        orden.setEstado("Completa");
        orden.setTotal(0.0f);

        orden = ordenRepository.save(orden);

        Float totalAcumulado = 0.0f;

        // 2. Guardar cada ItemOrden individualmente en su tabla
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (ItemOrdenRequest itemReq : request.getItems()) {
                Producto producto = productoRepository.findById(itemReq.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + itemReq.getProductoId()));

                if (producto.getStock() < itemReq.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
                }

                // Descontar stock
                producto.setStock(producto.getStock() - itemReq.getCantidad());
                productoRepository.save(producto);

                Float precioUnitario = producto.getPrecio();


                ItemOrden itemOrden = new ItemOrden();
                itemOrden.setOrden(orden);
                itemOrden.setProducto(producto);
                itemOrden.setCantidad(itemReq.getCantidad());
                itemOrden.setPrecio_unitario(precioUnitario);

                itemOrdenRepository.save(itemOrden);

                totalAcumulado = totalAcumulado + (precioUnitario * itemReq.getCantidad());
            }
        }

        // 3. Actualizar total final de la orden
        orden.setTotal(totalAcumulado);
        return ordenRepository.save(orden);
    }

    @Override
    public Orden getOrdenById(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + id));
    }

    @Override
    public List<Orden> getOrdenesByUsuarioId(Long usuarioId) {
        return ordenRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<Orden> getAllOrdenes() {
        return ordenRepository.findAll();
    }

    @Override
    public Orden cancelOrden(Long id) {
        Orden orden = getOrdenById(id);
        orden.setEstado("Cancelada");
        return ordenRepository.save(orden);
    }
}