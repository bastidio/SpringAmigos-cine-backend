package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Funcion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FuncionService {
    List<Funcion> getFunciones();
    Optional<Funcion> getFuncionById(Long id);
    Funcion createFuncion(Long peliculaId, Long salaId, LocalDateTime horario, String idioma, String formato, Float precioBase);
}