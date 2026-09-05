package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Entrada;
import com.uade.tpo.demo.entity.ItemOrden;
import com.uade.tpo.demo.entity.Orden;
import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.entity.Rol;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.exceptions.OrdenAccesoDenegadoException;
import com.uade.tpo.demo.exceptions.OrdenNotFoundException;
import com.uade.tpo.demo.repository.EntradaRepository;
import com.uade.tpo.demo.repository.ItemOrdenRepository;
import com.uade.tpo.demo.repository.OrdenRepository;
import com.uade.tpo.demo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrdenServiceIMPL implements OrdenService {

    // Convencion de estados en MAYUSCULAS, igual que el checkout (CONFIRMADA).
    private static final String ESTADO_CONFIRMADA = "CONFIRMADA";
    private static final String ESTADO_CANCELADA = "CANCELADA";

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private ItemOrdenRepository itemOrdenRepository;

    @Autowired
    private EntradaRepository entradaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public Orden getOrdenById(Long id, Usuario solicitante)
            throws OrdenAccesoDenegadoException, OrdenNotFoundException {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(OrdenNotFoundException::new);
        validarPertenencia(orden, solicitante);
        return orden;
    }

    @Override
    public List<Orden> getOrdenesByUsuarioId(Long usuarioId) {
        return ordenRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<Orden> getAllOrdenes() {
        return ordenRepository.findAll();
    }

    // Cancelar no es solo cambiar un string: hay que deshacer lo que el checkout
    // hizo. Se borran las Entrada de la orden (para liberar las butacas, que el
    // UNIQUE mantiene bloqueadas mientras exista la fila) y se devuelve el stock
    // de cada ItemOrden de producto. rollbackFor = Exception.class para que, si
    // algo falla en el medio, no quede una cancelacion a medias.
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orden cancelOrden(Long id, Usuario solicitante)
            throws OrdenAccesoDenegadoException, OrdenNotFoundException {
        Orden orden = getOrdenById(id, solicitante);

        // Guarda de estado: solo se cancela una orden confirmada. Sin esto, una
        // segunda llamada devolveria el stock otra vez (doble reintegro).
        if (!ESTADO_CONFIRMADA.equals(orden.getEstado())) {
            throw new IllegalStateException(
                    "La orden " + id + " no se puede cancelar porque su estado es " + orden.getEstado());
        }

        // 1. Liberar butacas: borrar las entradas emitidas por esta orden.
        // Query by Example: un "molde" de Entrada con solo la orden seteada; QBE
        // ignora los campos null y matchea por la asociacion orden_id.
        Entrada molde = new Entrada();
        molde.setOrden_id(orden);
        List<Entrada> entradas = entradaRepository.findAll(Example.of(molde));
        if (!entradas.isEmpty()) {
            entradaRepository.deleteAll(entradas);
        }

        // 2. Devolver el stock de los productos comprados.
        List<ItemOrden> items = itemOrdenRepository.findByOrdenId(id);
        for (ItemOrden item : items) {
            Producto producto = item.getProducto();
            if (producto != null && item.getCantidad() != null) {
                int stockActual = producto.getStock() != null ? producto.getStock() : 0;
                producto.setStock(stockActual + item.getCantidad());
                productoRepository.save(producto);
            }
        }

        orden.setEstado(ESTADO_CANCELADA);
        return ordenRepository.save(orden);
    }

    private void validarPertenencia(Orden orden, Usuario solicitante) throws OrdenAccesoDenegadoException {
        boolean esAdmin = solicitante.getRol() == Rol.ADMIN;
        boolean esDueño = orden.getUsuario() != null && orden.getUsuario().getId().equals(solicitante.getId());

        if (!esAdmin && !esDueño) {
            throw new OrdenAccesoDenegadoException();
        }
    }
}
