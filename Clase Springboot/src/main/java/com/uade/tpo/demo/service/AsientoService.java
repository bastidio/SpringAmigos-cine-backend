package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Asiento;

import com.uade.tpo.demo.exceptions.AsientoNotFoundException;
import com.uade.tpo.demo.exceptions.SalaConFuncionesException;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;

import java.util.List;
import java.util.Optional;

public interface AsientoService {
    List<Asiento> getAsientos();
    Optional<Asiento> getAsientoById(Long id);
        Asiento createAsiento(Long salaId, Integer fila, Integer numero) throws SalaNotFoundException;

    // Solo se puede quitar una butaca de una sala sin funciones (si no, podria tener entradas vendidas).
    void deleteAsiento(Long id) throws AsientoNotFoundException, SalaConFuncionesException;
}