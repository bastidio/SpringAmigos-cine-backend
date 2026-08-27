package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Funcion;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface FuncionService {
    List<Funcion> getFunciones();
    Optional<Funcion> getFuncionById(Long id);
    Funcion createFuncion(Long peliculaId, Long salaId, Date horario, String idioma, String formato, Float precioBase);
}