package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Categoria;
import com.uade.tpo.demo.exceptions.CategoriaDuplicateException;
import com.uade.tpo.demo.exceptions.CategoriaNotFoundException;
import com.uade.tpo.demo.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServiceIMPL implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> getCategorias() {
        return categoriaRepository.findAll();
    }

    @Override
    public Categoria getCategoriaById(Long id) throws CategoriaNotFoundException {
        return categoriaRepository.findById(id)
                .orElseThrow(CategoriaNotFoundException::new);
    }

    @Override
    public Categoria createCategoria(String nombre) throws CategoriaDuplicateException {
        boolean categoriaDuplicada = categoriaRepository.findAll().stream()
                .anyMatch(categoria -> categoria.getNombre() != null
                        && categoria.getNombre().equalsIgnoreCase(nombre));

        if (categoriaDuplicada) {
            throw new CategoriaDuplicateException();
        }

        Categoria nuevaCategoria = Categoria.builder()
                .nombre(nombre)
                .build();

        return categoriaRepository.save(nuevaCategoria);
    }
}