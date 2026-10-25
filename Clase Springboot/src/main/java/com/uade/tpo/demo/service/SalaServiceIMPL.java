package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Sala;
import com.uade.tpo.demo.entity.dto.AsientoSalaResponse;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;
import com.uade.tpo.demo.exceptions.ValidacionException;
import com.uade.tpo.demo.repository.AsientoRepository;
import com.uade.tpo.demo.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SalaServiceIMPL implements SalaService {

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private AsientoRepository asientoRepository;

    @Override
    public List<Sala> getSalas() {
        return salaRepository.findAll();
    }

    @Override
    public Optional<Sala> getSalaById(Long id) {
        return salaRepository.findById(id);
    }

    @Override
    public Sala createSala(String nombre, Integer capacidad) {
        Sala nuevaSala = new Sala();
        nuevaSala.setNombre(nombre);
        nuevaSala.setCapacidad(capacidad);

        return salaRepository.save(nuevaSala);
    }

    @Override
    public Sala updateSala(Long id, String nombre, Integer capacidad) throws SalaNotFoundException {
        Map<String, String> errores = new LinkedHashMap<>();
        if (nombre == null || nombre.isBlank()) {
            errores.put("nombre", "el nombre es obligatorio");
        }
        if (capacidad == null) {
            errores.put("capacidad", "la capacidad es obligatoria");
        } else if (capacidad <= 0) {
            errores.put("capacidad", "la capacidad debe ser mayor a 0");
        }
        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }

        Sala sala = salaRepository.findById(id).orElseThrow(SalaNotFoundException::new);
        sala.setNombre(nombre);
        sala.setCapacidad(capacidad);
        return salaRepository.save(sala);
    }

    @Override
    public List<AsientoSalaResponse> getAsientosDeSala(Long salaId) throws SalaNotFoundException {
        // Sin este chequeo una sala inexistente devolveria [] en vez de 404.
        if (!salaRepository.existsById(salaId)) {
            throw new SalaNotFoundException();
        }
        return asientoRepository.findBySalaIdOrderByFilaAscNumeroAsc(salaId).stream()
                .map(a -> new AsientoSalaResponse(a.getId(), a.getFila(), a.getNumero()))
                .toList();
    }
}
