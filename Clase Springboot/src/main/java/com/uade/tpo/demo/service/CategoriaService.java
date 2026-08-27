package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriaService {
    List<Categoria> getCategorias();
    Optional<Categoria> getCategoriaById(Long id);
    Categoria createCategoria(String nombre);
}