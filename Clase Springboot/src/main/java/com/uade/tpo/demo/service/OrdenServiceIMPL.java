package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Entrada;
import com.uade.tpo.demo.entity.ItemOrden;
import com.uade.tpo.demo.entity.Orden;
import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.entity.Rol;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.dto.EntradaResponse;
import com.uade.tpo.demo.entity.dto.ItemOrdenResponse;
import com.uade.tpo.demo.entity.dto.OrdenResponse;
import com.uade.tpo.demo.exceptions.OrdenAccesoDenegadoException;
import com.uade.tpo.demo.exceptions.OrdenNotFoundException;
import com.uade.tpo.demo.repository.EntradaRepository;
import com.uade.tpo.demo.repository.ItemOrdenRepository;
import com.uade.tpo.demo.repository.OrdenRepository;
import com.uade.tpo.demo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Override
    public OrdenResponse getOrdenResponseById(Long id, Usuario solicitante)
            throws OrdenAccesoDenegadoException, OrdenNotFoundException {
        return buildOrdenResponse(getOrdenById(id, solicitante));
    }

    @Override
    public List<OrdenResponse> getOrdenesResponseByUsuarioId(Long usuarioId) {
        return getOrdenesByUsuarioId(usuarioId).stream()
                .map(this::buildOrdenResponse)
                .toList();
    }

    @Override
    public List<OrdenResponse> getAllOrdenesResponse() {
        return getAllOrdenes().stream()
                .map(this::buildOrdenResponse)
                .toList();
    }

    // Cancelar no es solo cambiar un string: hay que deshacer lo que el checkout
    // hizo. Se borran las Entrada de la orden (para liberar las butacas, que el
    // UNIQUE mantiene bloqueadas mientras exista la fila) y se devuelve el stock
    // de cada ItemOrden de producto. rollbackFor = Exception.class para que, si
    // algo falla en el medio, no quede una cancelacion a medias.
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrdenResponse cancelOrden(Long id, Usuario solicitante)
            throws OrdenAccesoDenegadoException, OrdenNotFoundException {
        Orden orden = getOrdenById(id, solicitante);

        // Guarda de estado: solo se cancela una orden confirmada. Sin esto, una
        // segunda llamada devolveria el stock otra vez (doble reintegro).
        if (!ESTADO_CONFIRMADA.equals(orden.getEstado())) {
            throw new IllegalStateException(
                    "La orden " + id + " no se puede cancelar porque su estado es " + orden.getEstado());
        }

        // 1. Liberar butacas: borrar las entradas emitidas por esta orden. Se
        // capturan antes de borrarlas para poder mostrarlas en la respuesta.
        List<Entrada> entradas = entradaRepository.findByOrden(orden);
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
        Orden ordenCancelada = ordenRepository.save(orden);
        return buildOrdenResponse(ordenCancelada, items, entradas);
    }

    private void validarPertenencia(Orden orden, Usuario solicitante) throws OrdenAccesoDenegadoException {
        boolean esAdmin = solicitante.getRol() == Rol.ADMIN;
        boolean esDueño = orden.getUsuario() != null && orden.getUsuario().getId().equals(solicitante.getId());

        if (!esAdmin && !esDueño) {
            throw new OrdenAccesoDenegadoException();
        }
    }

    // Arma el detalle de la orden a partir de las consultas que ya usa
    // cancelOrden: ItemOrdenRepository.findByOrdenId y EntradaRepository.findByOrden,
    // ya que Orden no tiene esas colecciones mapeadas.
    private OrdenResponse buildOrdenResponse(Orden orden) {
        List<ItemOrden> items = itemOrdenRepository.findByOrdenId(orden.getId());
        List<Entrada> entradas = entradaRepository.findByOrden(orden);
        return buildOrdenResponse(orden, items, entradas);
    }

    private OrdenResponse buildOrdenResponse(Orden orden, List<ItemOrden> items, List<Entrada> entradas) {
        List<ItemOrdenResponse> productos = items.stream()
                .map(this::mapItemToResponse)
                .toList();
        List<EntradaResponse> entradasResponse = entradas.stream()
                .map(this::mapEntradaToResponse)
                .toList();
        return new OrdenResponse(orden.getId(), orden.getFecha(), orden.getTotal(), orden.getEstado(),
                productos, entradasResponse);
    }

    private ItemOrdenResponse mapItemToResponse(ItemOrden item) {
        BigDecimal precioUnitario = item.getPrecio_unitario() != null ? item.getPrecio_unitario() : BigDecimal.ZERO;
        int cantidad = item.getCantidad() != null ? item.getCantidad() : 0;
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        String nombre = item.getProducto() != null ? item.getProducto().getNombre() : null;

        return new ItemOrdenResponse(
                nombre,
                item.getCantidad(),
                precioUnitario.setScale(2, RoundingMode.HALF_UP),
                subtotal.setScale(2, RoundingMode.HALF_UP));
    }

    // Mismo mapeo que EntradaServiceIMPL.mapToResponse: pelicula, sala y horario
    // salen de la funcion; fila y numero, del asiento.
    private EntradaResponse mapEntradaToResponse(Entrada entrada) {
        return new EntradaResponse(
                entrada.getId(),
                entrada.getFuncion().getPelicula().getTitulo(),
                entrada.getFuncion().getSala().getNombre(),
                entrada.getFuncion().getHorario(),
                entrada.getAsiento().getFila(),
                entrada.getAsiento().getNumero(),
                entrada.getPrecio());
    }
}
