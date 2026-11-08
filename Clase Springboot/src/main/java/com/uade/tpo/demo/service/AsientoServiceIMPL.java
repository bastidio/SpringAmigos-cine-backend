package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Asiento;
import com.uade.tpo.demo.entity.Sala;
import com.uade.tpo.demo.repository.AsientoRepository;
import com.uade.tpo.demo.repository.FuncionRepository;
import com.uade.tpo.demo.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.exceptions.AsientoNotFoundException;
import com.uade.tpo.demo.exceptions.SalaConFuncionesException;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class AsientoServiceIMPL implements AsientoService {

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private FuncionRepository funcionRepository;

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
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(SalaNotFoundException::new);

        Asiento nuevoAsiento = new Asiento();
        nuevoAsiento.setSala(sala);
        nuevoAsiento.setFila(fila);
        nuevoAsiento.setNumero(numero);

        return asientoRepository.save(nuevoAsiento);
    }

    @Override
    public void deleteAsiento(Long id) throws AsientoNotFoundException, SalaConFuncionesException {
        Asiento asiento = asientoRepository.findById(id).orElseThrow(AsientoNotFoundException::new);

        // Si la sala tiene alguna funcion (pasada o futura), la butaca puede tener entradas: no se toca.
        boolean salaConFunciones = funcionRepository.findAll().stream()
                .anyMatch(funcion -> funcion.getSala() != null
                        && funcion.getSala().getId().equals(asiento.getSala().getId()));
        if (salaConFunciones) {
            throw new SalaConFuncionesException();
        }

        asientoRepository.delete(asiento);
    }
}