package com.uade.tpo.demo.service;


import com.uade.tpo.demo.entity.Orden;
import com.uade.tpo.demo.entity.Usuario;
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
    Orden cancelOrden(Long id, Usuario solicitante) throws OrdenAccesoDenegadoException, OrdenNotFoundException;

}
