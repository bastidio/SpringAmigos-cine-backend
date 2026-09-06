package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Asiento;
import com.uade.tpo.demo.entity.Sala;
import com.uade.tpo.demo.repository.AsientoRepository;
import com.uade.tpo.demo.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.exceptions.SalaNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class AsientoServiceIMPL implements AsientoService {

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Override
    public List<Asiento> getAsientos() {
        return asientoRepository.findAll();
    }

    @Override
    public Optional<Asiento> getAsientoById(Long id) {
        return asientoRepository.findById(id);
    }

    @Override
    public Asiento createAsiento(Long salaId, Integer fila, Integer numero) throws SalaNotFoundException {
        // orElseThrow() pelado lanza NoSuchElementException, que nadie maneja y
        // sale como 500 con stacktrace. Con la excepcion propia devuelve 404.
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(SalaNotFoundException::new);

        Asiento nuevoAsiento = new Asiento();
        nuevoAsiento.setSala(sala);
        nuevoAsiento.setFila(fila);
        nuevoAsiento.setNumero(numero);

        return asientoRepository.save(nuevoAsiento);
    }
}