package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Funcion;
import com.uade.tpo.demo.entity.Pelicula;
import com.uade.tpo.demo.entity.Sala;
import com.uade.tpo.demo.repository.FuncionRepository;
import com.uade.tpo.demo.repository.PeliculaRepository;
import com.uade.tpo.demo.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public Optional<Funcion> getFuncionById(Long id) {
        return funcionRepository.findById(id);
    }

    @Override
    public Funcion createFuncion(Long peliculaId, Long salaId, LocalDateTime horario, String idioma, String formato, Float precioBase) {
        Pelicula pelicula = peliculaRepository.findById(peliculaId).orElseThrow();
        Sala sala = salaRepository.findById(salaId).orElseThrow();

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