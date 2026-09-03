package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Funcion;
import com.uade.tpo.demo.exceptions.FuncionDuplicateException;
import com.uade.tpo.demo.exceptions.FuncionNotFoundException;
import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;

import java.sql.Date;
import java.util.List;

public interface FuncionService {
    List<Funcion> getFunciones();
    Funcion getFuncionById(Long id) throws FuncionNotFoundException;
    Funcion createFuncion(Long peliculaId, Long salaId, Date horario, String idioma, String formato, Float precioBase)
            throws PeliculaNotFoundException, SalaNotFoundException, FuncionDuplicateException;
}