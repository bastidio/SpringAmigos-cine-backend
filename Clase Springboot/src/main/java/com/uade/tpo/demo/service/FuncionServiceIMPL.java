package com.uade.tpo.demo.service;
import java.math.BigDecimal;
import com.uade.tpo.demo.entity.Funcion;
import com.uade.tpo.demo.entity.Pelicula;
import com.uade.tpo.demo.entity.Sala;
import com.uade.tpo.demo.entity.dto.FuncionResponse;
import com.uade.tpo.demo.exceptions.*;
import com.uade.tpo.demo.repository.EntradaRepository;
import com.uade.tpo.demo.repository.FuncionRepository;
import com.uade.tpo.demo.repository.PeliculaRepository;
import com.uade.tpo.demo.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FuncionServiceIMPL implements FuncionService {

    // Despues de cada funcion la sala queda ocupada este tiempo para la limpieza.
    private static final int MINUTOS_LIMPIEZA = 15;

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
        validarDatosFuncion(peliculaId, salaId, horario, precioBase);

        Pelicula pelicula = peliculaRepository.findById(peliculaId)
                .orElseThrow(PeliculaNotFoundException::new);
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(SalaNotFoundException::new);
        validarSalaLibre(salaId, horario, pelicula.getDuracion(), null);

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
        validarSalaLibre(salaId, horario, pelicula.getDuracion(), id);

        // Las entradas vendidas apuntan a asientos de la sala actual: cambiarla las dejaria inconsistentes.
        boolean cambiaSala = funcion.getSala() == null || !funcion.getSala().getId().equals(salaId);
        if (cambiaSala && entradaRepository.countByFuncionId(id) > 0) {
            throw new IllegalStateException(
                    "No se puede cambiar la sala de la función porque ya tiene entradas vendidas.");
        }

        funcion.setPelicula(pelicula);
        funcion.setSala(sala);
        funcion.setHorario(horario);
        funcion.setIdioma(idioma);
        funcion.setFormato(formato);
        funcion.setPrecio_base(precioBase);

        return funcionRepository.save(funcion);
    }

    @Override
    public FuncionResponse toResponse(Funcion funcion) {
        return mapToResponse(funcion);
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

    // Una funcion ocupa la sala desde su horario hasta que termina la pelicula mas la limpieza.
    private LocalDateTime finDeOcupacion(LocalDateTime horario, Integer duracionMinutos) {
        int duracion = duracionMinutos == null ? 0 : duracionMinutos;
        return horario.plusMinutes(duracion + MINUTOS_LIMPIEZA);
    }

    // Al editar se excluye la propia funcion; al crear idFuncionExcluir es null.
    private void validarSalaLibre(Long salaId, LocalDateTime horario, Integer duracionMinutos, Long idFuncionExcluir) {
        if (horario == null) {
            return;
        }
        LocalDateTime inicioNueva = horario;
        LocalDateTime finNueva = finDeOcupacion(horario, duracionMinutos);
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

        for (Funcion existente : funcionRepository.findBySalaId(salaId)) {
            if (existente.getId().equals(idFuncionExcluir)) {
                continue;
            }
            LocalDateTime inicioExistente = existente.getHorario();
            LocalDateTime finExistente = finDeOcupacion(inicioExistente, existente.getPelicula().getDuracion());
            if (inicioNueva.isBefore(finExistente) && inicioExistente.isBefore(finNueva)) {
                throw new IllegalStateException("Esta función terminaría a las " + finNueva.format(formatoHora)
                        + " y se superpone con otra función de "
                        + inicioExistente.format(formatoHora) + " a " + finExistente.format(formatoHora) + " en esta sala.");
            }
        }
    }

    // Mismo criterio que ProductoServiceIMPL: junta los errores y corta con 400.
    private void validarDatosFuncion(Long peliculaId, Long salaId, LocalDateTime horario, BigDecimal precioBase) {
        Map<String, String> errores = new LinkedHashMap<>();

        if (peliculaId == null) {
            errores.put("pelicula_id", "La película es obligatoria.");
        }
        if (salaId == null) {
            errores.put("sala_id", "La sala es obligatoria.");
        }
        if (horario == null) {
            errores.put("horario", "El horario es obligatorio.");
        }
        if (precioBase == null) {
            errores.put("precio_base", "El precio base es obligatorio.");
        } else if (precioBase.signum() < 0) {
            errores.put("precio_base", "El precio base no puede ser negativo.");
        }

        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }
    }

    @Override
    public void deleteFuncion(Long id) throws FuncionNotFoundException, FuncionConEntradasException {
        if (!funcionRepository.existsById(id)) {
            throw new FuncionNotFoundException();
        }

        if (entradaRepository.countByFuncionId(id) > 0) {
            throw new FuncionConEntradasException("No se puede eliminar la función porque ya tiene entradas vendidas.");
        }

        funcionRepository.deleteById(id);
    }
}
