package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Sala;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;
import com.uade.tpo.demo.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaServiceIMPL implements SalaService {

    @Autowired
    private SalaRepository salaRepository; 

    @Override
    public List<Sala> getSalas() {
        return salaRepository.findAll(); 
    }

    @Override
    public Sala getSalaById(Long id) throws SalaNotFoundException {
        return salaRepository.findById(id)
                .orElseThrow(SalaNotFoundException::new);
    }

    @Override
    public Sala createSala(String nombre, Integer capacidad) {
        Sala nuevaSala = new Sala();
        nuevaSala.setNombre(nombre);
        nuevaSala.setCapacidad(capacidad);
        
        return salaRepository.save(nuevaSala); 
    }
}