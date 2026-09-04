package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Entrada;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.exceptions.EntradaAccesoDenegadoException;
import com.uade.tpo.demo.exceptions.EntradaNotFoundException;

import java.util.List;

public interface EntradaService {

    // "solicitante" es el usuario autenticado. getEntradaById y getEntradasByUsuarioId
    // validan que sea el dueño de la entrada (via orden.usuario) o tenga rol ADMIN,
    // mismo criterio anti-IDOR que OrdenService.
    Entrada getEntradaById(Long id, Usuario solicitante)
            throws EntradaNotFoundException, EntradaAccesoDenegadoException;

    // Historial de entradas del usuario autenticado.
    List<Entrada> getMisEntradas(Usuario solicitante);

    // Entradas de un usuario puntual. Solo ADMIN o el propio usuario (anti-IDOR).
    List<Entrada> getEntradasByUsuarioId(Long usuarioId, Usuario solicitante)
            throws EntradaAccesoDenegadoException;

    // Ocupación de butacas de una función: qué asientos ya fueron vendidos.
    List<Entrada> getEntradasByFuncion(Long funcionId);
}