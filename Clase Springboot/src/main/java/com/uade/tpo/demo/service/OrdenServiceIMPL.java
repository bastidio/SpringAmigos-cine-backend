package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Orden;
import com.uade.tpo.demo.exceptions.OrdenNotFoundException;
import com.uade.tpo.demo.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenServiceIMPL implements OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    @Override
    public Orden getOrdenById(Long id) throws OrdenNotFoundException {
        return ordenRepository.findById(id)
                .orElseThrow(OrdenNotFoundException::new);
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
    public Orden cancelOrden(Long id) throws OrdenNotFoundException {
        Orden orden = getOrdenById(id);
        orden.setEstado("Cancelada");
        return ordenRepository.save(orden);
    }
}
