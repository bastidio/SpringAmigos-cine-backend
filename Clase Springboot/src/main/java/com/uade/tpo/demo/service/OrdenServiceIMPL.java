package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Orden;
import com.uade.tpo.demo.entity.Rol;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.exceptions.OrdenAccesoDenegadoException;
import com.uade.tpo.demo.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenServiceIMPL implements OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    @Override
    public Orden getOrdenById(Long id, Usuario solicitante) throws OrdenAccesoDenegadoException {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + id));
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
    public Orden cancelOrden(Long id, Usuario solicitante) throws OrdenAccesoDenegadoException {
        Orden orden = getOrdenById(id, solicitante);
        orden.setEstado("Cancelada");
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
