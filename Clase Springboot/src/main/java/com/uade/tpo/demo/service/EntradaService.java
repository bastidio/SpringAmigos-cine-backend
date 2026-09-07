package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.dto.EntradaResponse;
import com.uade.tpo.demo.entity.dto.OcupacionFuncionResponse;
import com.uade.tpo.demo.exceptions.EntradaAccesoDenegadoException;
import com.uade.tpo.demo.exceptions.EntradaNotFoundException;

import java.util.List;

public interface EntradaService {

    // dueño de la entrada o ADMIN.
    EntradaResponse getEntradaById(Long id, Usuario solicitante)
            throws EntradaNotFoundException, EntradaAccesoDenegadoException;

    List<EntradaResponse> getMisEntradas(Usuario solicitante);

    // Solo ADMIN o el propio usuario.
    List<EntradaResponse> getEntradasByUsuarioId(Long usuarioId, Usuario solicitante)
            throws EntradaAccesoDenegadoException;

    // Endpoint publico: solo ids de asiento, sin datos del comprador.
    OcupacionFuncionResponse getAsientosOcupados(Long funcionId);
}