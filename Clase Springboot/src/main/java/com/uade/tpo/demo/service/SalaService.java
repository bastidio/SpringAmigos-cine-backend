package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Sala;
import com.uade.tpo.demo.entity.dto.AsientoSalaResponse;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;

import java.util.List;
import java.util.Optional;

public interface SalaService {
    List<Sala> getSalas();
    Optional<Sala> getSalaById(Long id);
    Sala createSala(String nombre, Integer capacidad);
    Sala updateSala(Long id, String nombre, Integer capacidad) throws SalaNotFoundException;

    // Butacas de la sala para el mapa del panel admin.
    List<AsientoSalaResponse> getAsientosDeSala(Long salaId) throws SalaNotFoundException;
}
