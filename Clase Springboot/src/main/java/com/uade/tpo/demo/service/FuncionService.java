package com.uade.tpo.demo.service;
import java.math.BigDecimal;
import com.uade.tpo.demo.entity.Funcion;
import com.uade.tpo.demo.entity.dto.FuncionResponse;
import com.uade.tpo.demo.exceptions.FuncionNotFoundException;
import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FuncionService {
    List<Funcion> getFunciones();

    // fecha null = todas. Incluye entradasVendidas para el panel admin.
    List<FuncionResponse> getFuncionesResponse(LocalDate fecha);

    Optional<Funcion> getFuncionById(Long id);
    Funcion createFuncion(Long peliculaId, Long salaId, LocalDateTime horario, String idioma, String formato, BigDecimal precioBase)
        throws PeliculaNotFoundException, SalaNotFoundException;

    Funcion updateFuncion(Long id, Long peliculaId, Long salaId, LocalDateTime horario, String idioma, String formato, BigDecimal precioBase)
        throws FuncionNotFoundException, PeliculaNotFoundException, SalaNotFoundException;
}
