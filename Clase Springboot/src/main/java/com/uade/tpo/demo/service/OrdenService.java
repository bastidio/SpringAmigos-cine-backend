package com.uade.tpo.demo.service;


import com.uade.tpo.demo.entity.Orden;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.dto.OrdenResponse;
import com.uade.tpo.demo.exceptions.OrdenAccesoDenegadoException;
import com.uade.tpo.demo.exceptions.OrdenNotFoundException;
import java.util.List;

public interface OrdenService {
    // "solicitante" es el usuario autenticado: getOrdenById y cancelOrden
    // validan que sea el dueño de la orden o tenga rol ADMIN (evita el IDOR
    // de leer/cancelar ordenes ajenas).
    Orden getOrdenById(Long id, Usuario solicitante) throws OrdenAccesoDenegadoException, OrdenNotFoundException;
    List<Orden> getOrdenesByUsuarioId(Long usuarioId);
    List<Orden> getAllOrdenes();

    // Mismas consultas que las anteriores, pero con el detalle de la compra
    // (productos y entradas) resuelto para el frontend. Son las que expone
    // OrdenController.
    OrdenResponse getOrdenResponseById(Long id, Usuario solicitante)
            throws OrdenAccesoDenegadoException, OrdenNotFoundException;
    List<OrdenResponse> getOrdenesResponseByUsuarioId(Long usuarioId);
    List<OrdenResponse> getAllOrdenesResponse();

    OrdenResponse cancelOrden(Long id, Usuario solicitante) throws OrdenAccesoDenegadoException, OrdenNotFoundException;

}
