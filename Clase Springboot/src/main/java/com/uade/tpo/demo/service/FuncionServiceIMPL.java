package com.uade.tpo.demo.service;
import java.math.BigDecimal;
import com.uade.tpo.demo.entity.Funcion;
import com.uade.tpo.demo.entity.Pelicula;
import com.uade.tpo.demo.entity.Sala;
import com.uade.tpo.demo.entity.dto.FuncionResponse;
import com.uade.tpo.demo.repository.EntradaRepository;
import com.uade.tpo.demo.repository.FuncionRepository;
import com.uade.tpo.demo.repository.PeliculaRepository;
import com.uade.tpo.demo.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.exceptions.FuncionNotFoundException;
import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;
import com.uade.tpo.demo.exceptions.ValidacionException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FuncionServiceIMPL implements FuncionService {

    @Autowired
    private FuncionRepository funcionRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private EntradaRepository entradaRepository;

    @Override
    public List<Funcion> getFunciones() {
        return funcionRepository.findAll();
    }

    @Override
    public List<FuncionResponse> getFuncionesResponse(LocalDate fecha) {
        List<Funcion> funciones = fecha == null
                ? funcionRepository.findAll()
                : funcionRepository.findByHorarioGreaterThanEqualAndHorarioLessThanOrderByHorarioAsc(
                        fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay());
        return funciones.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Optional<Funcion> getFuncionById(Long id) {
        return funcionRepository.findById(id);
    }

    @Override
    public Funcion createFuncion(Long peliculaId, Long salaId, LocalDateTime horario, String idioma, String formato, BigDecimal precioBase)
            throws PeliculaNotFoundException, SalaNotFoundException {

        Pelicula pelicula = peliculaRepository.findById(peliculaId)
                .orElseThrow(PeliculaNotFoundException::new);
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(SalaNotFoundException::new);

        Funcion nuevaFuncion = new Funcion();
        nuevaFuncion.setPelicula(pelicula);
        nuevaFuncion.setSala(sala);
        nuevaFuncion.setHorario(horario);
        nuevaFuncion.setIdioma(idioma);
        nuevaFuncion.setFormato(formato);
        nuevaFuncion.setPrecio_base(precioBase);

        return funcionRepository.save(nuevaFuncion);
    }

    @Override
    public Funcion updateFuncion(Long id, Long peliculaId, Long salaId, LocalDateTime horario, String idioma, String formato, BigDecimal precioBase)
            throws FuncionNotFoundException, PeliculaNotFoundException, SalaNotFoundException {
        validarDatosFuncion(peliculaId, salaId, horario, precioBase);

        Funcion funcion = funcionRepository.findById(id).orElseThrow(FuncionNotFoundException::new);
        Pelicula pelicula = peliculaRepository.findById(peliculaId)
                .orElseThrow(PeliculaNotFoundException::new);
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(SalaNotFoundException::new);

        // Las entradas vendidas apuntan a asientos de la sala actual: cambiarla las dejaria inconsistentes.
        boolean cambiaSala = funcion.getSala() == null || !funcion.getSala().getId().equals(salaId);
        if (cambiaSala && entradaRepository.countByFuncionId(id) > 0) {
            throw new IllegalStateException(
                    "No se puede cambiar la sala de la funcion porque ya tiene entradas vendidas");
        }

        funcion.setPelicula(pelicula);
        funcion.setSala(sala);
        funcion.setHorario(horario);
        funcion.setIdioma(idioma);
        funcion.setFormato(formato);
        funcion.setPrecio_base(precioBase);

        return funcionRepository.save(funcion);
    }

    private FuncionResponse mapToResponse(Funcion funcion) {
        return new FuncionResponse(
                funcion.getId(),
                funcion.getPelicula(),
                funcion.getSala(),
                funcion.getHorario(),
                funcion.getIdioma(),
                funcion.getFormato(),
                funcion.getPrecio_base(),
                entradaRepository.countByFuncionId(funcion.getId()));
    }

    // Mismo criterio que ProductoServiceIMPL: junta los errores y corta con 400.
    private void validarDatosFuncion(Long peliculaId, Long salaId, LocalDateTime horario, BigDecimal precioBase) {
        Map<String, String> errores = new LinkedHashMap<>();

        if (peliculaId == null) {
            errores.put("pelicula_id", "la pelicula es obligatoria");
        }
        if (salaId == null) {
            errores.put("sala_id", "la sala es obligatoria");
        }
        if (horario == null) {
            errores.put("horario", "el horario es obligatorio");
        }
        if (precioBase == null) {
            errores.put("precio_base", "el precio base es obligatorio");
        } else if (precioBase.signum() < 0) {
            errores.put("precio_base", "el precio base no puede ser negativo");
        }

        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }
    }
}
