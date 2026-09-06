package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Entrada;
import com.uade.tpo.demo.entity.Rol;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.dto.OcupacionFuncionResponse;
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
        return entradaRepository.findByOrdenUsuarioId(solicitante.getId());
    }

    @Override
    public List<Entrada> getEntradasByUsuarioId(Long usuarioId, Usuario solicitante)
            throws EntradaAccesoDenegadoException {
        boolean esAdmin = solicitante.getRol() == Rol.ADMIN;
        boolean esPropio = solicitante.getId().equals(usuarioId);
        if (!esAdmin && !esPropio) {
            throw new EntradaAccesoDenegadoException();
        }
        return entradaRepository.findByOrdenUsuarioId(usuarioId);
    }

    @Override
    public OcupacionFuncionResponse getAsientosOcupados(Long funcionId) {
        // Se traen las entradas de la funcion y se proyecta solo el id de asiento:
        // la respuesta publica (OcupacionFuncionResponse) nunca lleva la orden ni
        // el comprador, solo la lista de butacas ocupadas.
        List<Long> asientosOcupados = entradaRepository.findByFuncionId(funcionId).stream()
                .map(entrada -> entrada.getAsiento().getId())
                .toList();
        return new OcupacionFuncionResponse(asientosOcupados);
    }

    // La entrada pertenece al usuario a través de su orden (orden.usuario).
    // Mismo patrón que OrdenServiceIMPL.validarPertenencia.
    private void validarPertenencia(Entrada entrada, Usuario solicitante)
            throws EntradaAccesoDenegadoException {
        boolean esAdmin = solicitante.getRol() == Rol.ADMIN;
        boolean esDueño = entrada.getOrden() != null
                && entrada.getOrden().getUsuario() != null
                && entrada.getOrden().getUsuario().getId().equals(solicitante.getId());

        if (!esAdmin && !esDueño) {
            throw new EntradaAccesoDenegadoException();
        }
    }
}