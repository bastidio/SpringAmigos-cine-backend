package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Entrada;
import com.uade.tpo.demo.entity.Rol;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.exceptions.EntradaAccesoDenegadoException;
import com.uade.tpo.demo.exceptions.EntradaNotFoundException;
import com.uade.tpo.demo.repository.EntradaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntradaServiceIMPL implements EntradaService {

    @Autowired
    private EntradaRepository entradaRepository;

    @Override
    public Entrada getEntradaById(Long id, Usuario solicitante)
            throws EntradaNotFoundException, EntradaAccesoDenegadoException {
        Entrada entrada = entradaRepository.findById(id)
                .orElseThrow(EntradaNotFoundException::new);
        validarPertenencia(entrada, solicitante);
        return entrada;
    }

    @Override
    public List<Entrada> getMisEntradas(Usuario solicitante) {
        return entradaRepository.findByUsuarioId(solicitante.getId());
    }

    @Override
    public List<Entrada> getEntradasByUsuarioId(Long usuarioId, Usuario solicitante)
            throws EntradaAccesoDenegadoException {
        boolean esAdmin = solicitante.getRol() == Rol.ADMIN;
        boolean esPropio = solicitante.getId().equals(usuarioId);
        if (!esAdmin && !esPropio) {
            throw new EntradaAccesoDenegadoException();
        }
        return entradaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<Entrada> getEntradasByFuncion(Long funcionId) {
        return entradaRepository.findByFuncionId(funcionId);
    }

    // La entrada pertenece al usuario a través de su orden (orden_id.usuario).
    // Mismo patrón que OrdenServiceIMPL.validarPertenencia.
    private void validarPertenencia(Entrada entrada, Usuario solicitante)
            throws EntradaAccesoDenegadoException {
        boolean esAdmin = solicitante.getRol() == Rol.ADMIN;
        boolean esDueño = entrada.getOrden_id() != null
                && entrada.getOrden_id().getUsuario() != null
                && entrada.getOrden_id().getUsuario().getId().equals(solicitante.getId());

        if (!esAdmin && !esDueño) {
            throw new EntradaAccesoDenegadoException();
        }
    }
}