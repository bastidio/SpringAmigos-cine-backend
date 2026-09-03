package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Asiento;
import com.uade.tpo.demo.exceptions.AsientoNotFoundException;
import com.uade.tpo.demo.exceptions.AsientoOcupadoException;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;

import java.util.List;

public interface AsientoService {
    List<Asiento> getAsientos();
    Asiento getAsientoById(Long id) throws AsientoNotFoundException;
    Asiento createAsiento(Long salaId, Integer fila, Integer numero) throws SalaNotFoundException, AsientoOcupadoException;
}