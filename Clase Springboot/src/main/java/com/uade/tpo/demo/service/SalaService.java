package com.uade.tpo.demo.service;

import com.uade.tpo.marketplace.entity.Sala;
import java.util.List;
import java.util.Optional;

public interface SalaService {
    List<Sala> getSalas();
    Optional<Sala> getSalaById(Long id);
    Sala createSala(String nombre, Integer capacidad);
}