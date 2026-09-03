package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Funcion;
import com.uade.tpo.demo.entity.Pelicula;
import com.uade.tpo.demo.entity.Sala;
import com.uade.tpo.demo.exceptions.FuncionDuplicateException;
import com.uade.tpo.demo.exceptions.FuncionNotFoundException;
import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;
import com.uade.tpo.demo.repository.FuncionRepository;
import com.uade.tpo.demo.repository.PeliculaRepository;
import com.uade.tpo.demo.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

@Service
public class FuncionServiceIMPL implements FuncionService {

    @Autowired
    private FuncionRepository funcionRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Override
    public List<Funcion> getFunciones() {
        return funcionRepository.findAll();
    }

    @Override
    public Funcion getFuncionById(Long id) throws FuncionNotFoundException {
        return funcionRepository.findById(id)
                .orElseThrow(FuncionNotFoundException::new);
    }

    @Override
    public Funcion createFuncion(Long peliculaId, Long salaId, Date horario, String idioma, String formato, Float precioBase)
            throws PeliculaNotFoundException, SalaNotFoundException, FuncionDuplicateException {
        Pelicula pelicula = peliculaRepository.findById(peliculaId)
                .orElseThrow(PeliculaNotFoundException::new);
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(SalaNotFoundException::new);

        boolean funcionDuplicada = funcionRepository.findAll().stream()
                .anyMatch(funcion -> Objects.equals(funcion.getPelicula().getId(), peliculaId)
                        && Objects.equals(funcion.getSala().getId(), salaId)
                        && Objects.equals(funcion.getHorario(), horario));

        if (funcionDuplicada) {
            throw new FuncionDuplicateException();
        }

        Funcion nuevaFuncion = new Funcion();
        nuevaFuncion.setPelicula(pelicula);
        nuevaFuncion.setSala(sala);
        nuevaFuncion.setHorario(horario);
        nuevaFuncion.setIdioma(idioma);
        nuevaFuncion.setFormato(formato);
        nuevaFuncion.setPrecio_base(precioBase);

        return funcionRepository.save(nuevaFuncion);
    }
}