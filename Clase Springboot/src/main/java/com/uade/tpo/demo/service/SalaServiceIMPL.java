package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Sala;
import com.uade.tpo.demo.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalaServiceIMPL implements SalaService {

    @Autowired
    private SalaRepository salaRepository; 

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
}