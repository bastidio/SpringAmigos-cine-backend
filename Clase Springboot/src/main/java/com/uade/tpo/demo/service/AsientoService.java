package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Asiento;

import java.util.List;
import java.util.Optional;

public interface AsientoService {
    List<Asiento> getAsientos();
    Optional<Asiento> getAsientoById(Long id);
    Asiento createAsiento(Long salaId, Integer fila, Integer numero);
}