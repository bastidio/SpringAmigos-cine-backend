package com.uade.tpo.demo.service;
import java.math.BigDecimal;
import com.uade.tpo.demo.entity.Funcion;
import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FuncionService {
    List<Funcion> getFunciones();
    Optional<Funcion> getFuncionById(Long id);
    Funcion createFuncion(Long peliculaId, Long salaId, LocalDateTime horario, String idioma, String formato, BigDecimal precioBase)
        throws PeliculaNotFoundException, SalaNotFoundException;
}