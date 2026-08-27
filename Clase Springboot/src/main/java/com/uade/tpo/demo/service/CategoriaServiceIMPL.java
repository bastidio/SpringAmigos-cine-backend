package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Categoria;
import com.uade.tpo.demo.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceIMPL implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> getCategorias() {
        return categoriaRepository.findAll();
    }

    @Override
    public Optional<Categoria> getCategoriaById(Long id) {
        return categoriaRepository.findById(id);
    }

    @Override
    public Categoria createCategoria(String nombre) {
        Categoria nuevaCategoria = Categoria.builder()
                .nombre(nombre)
                .build();

        return categoriaRepository.save(nuevaCategoria);
    }
}