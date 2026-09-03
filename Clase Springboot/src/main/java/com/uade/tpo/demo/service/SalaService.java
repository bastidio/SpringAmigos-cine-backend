package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Sala;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;

import java.util.List;

public interface SalaService {
    List<Sala> getSalas();
    Sala getSalaById(Long id) throws SalaNotFoundException;
    Sala createSala(String nombre, Integer capacidad);
}