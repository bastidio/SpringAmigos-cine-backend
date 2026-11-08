package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Entrada;
import com.uade.tpo.demo.entity.ItemOrden;
import com.uade.tpo.demo.entity.Orden;
import com.uade.tpo.demo.entity.Producto;
import com.uade.tpo.demo.entity.Rol;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.dto.EntradaResponse;
import com.uade.tpo.demo.entity.dto.ItemOrdenDetalleResponse;
import com.uade.tpo.demo.entity.dto.ItemOrdenResponse;
import com.uade.tpo.demo.entity.dto.OrdenResponse;
import com.uade.tpo.demo.entity.dto.UsuarioResumenResponse;
import com.uade.tpo.demo.exceptions.OrdenAccesoDenegadoException;
import com.uade.tpo.demo.exceptions.OrdenNotFoundException;
import com.uade.tpo.demo.repository.EntradaRepository;
import com.uade.tpo.demo.repository.ItemOrdenRepository;
import com.uade.tpo.demo.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class OrdenServiceIMPL implements OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private ItemOrdenRepository itemOrdenRepository;

    @Autowired
    private EntradaRepository entradaRepository;

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

    private void validarPertenencia(Orden orden, Usuario solicitante) throws OrdenAccesoDenegadoException {
        boolean esAdmin = solicitante.getRol() == Rol.ADMIN;
        boolean esDueño = orden.getUsuario() != null && orden.getUsuario().getId().equals(solicitante.getId());

        if (!esAdmin && !esDueño) {
            throw new OrdenAccesoDenegadoException();
        }
    }

    // Orden no tiene mapeadas sus colecciones, por eso se consultan aparte.
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
        List<ItemOrdenDetalleResponse> itemsDetalle = items.stream()
                .map(this::mapItemToDetalle)
                .toList();
        return new OrdenResponse(orden.getId(), orden.getFecha(), orden.getTotal(), orden.getEstado(),
                productos, entradasResponse, mapUsuario(orden.getUsuario()), itemsDetalle,
                orden.getCodigoDescuento(), orden.getPorcentajeDescuento(), orden.getSubtotal(),
                orden.getMontoDescuento());
    }

    private UsuarioResumenResponse mapUsuario(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        // nombre + " " + apellido, igual que el perfil (UsuarioServiceIMPL).
        String nombre = usuario.getNombre() != null ? usuario.getNombre() : "";
        String apellido = usuario.getApellido() != null ? usuario.getApellido() : "";
        return new UsuarioResumenResponse(usuario.getId(), (nombre + " " + apellido).trim());
    }

    private ItemOrdenDetalleResponse mapItemToDetalle(ItemOrden item) {
        Producto producto = item.getProducto();
        ItemOrdenDetalleResponse.ProductoResumen productoResumen = producto != null
                ? new ItemOrdenDetalleResponse.ProductoResumen(producto.getId(), producto.getNombre())
                : null;
        return new ItemOrdenDetalleResponse(item.getCantidad(), item.getPrecio_unitario(), productoResumen);
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

    // Mismo mapeo que EntradaServiceIMPL.mapToResponse.
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
