package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Asiento;
import com.uade.tpo.demo.entity.Sala;
import com.uade.tpo.demo.exceptions.AsientoNotFoundException;
import com.uade.tpo.demo.exceptions.AsientoOcupadoException;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;
import com.uade.tpo.demo.repository.AsientoRepository;
import com.uade.tpo.demo.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
    public Asiento getAsientoById(Long id) throws AsientoNotFoundException {
        return asientoRepository.findById(id)
                .orElseThrow(AsientoNotFoundException::new);
    }

    @Override
    public Asiento createAsiento(Long salaId, Integer fila, Integer numero) throws SalaNotFoundException, AsientoOcupadoException {
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(SalaNotFoundException::new);

        boolean asientoDuplicado = asientoRepository.findAll().stream()
                .anyMatch(asiento -> Objects.equals(asiento.getSala().getId(), salaId)
                        && Objects.equals(asiento.getFila(), fila)
                        && Objects.equals(asiento.getNumero(), numero));

        if (asientoDuplicado) {
            throw new AsientoOcupadoException();
        }

        Asiento nuevoAsiento = new Asiento();
        nuevoAsiento.setSala(sala);
        nuevoAsiento.setFila(fila);
        nuevoAsiento.setNumero(numero);

        return asientoRepository.save(nuevoAsiento);
    }
}