package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Categoria;
import com.uade.tpo.demo.exceptions.CategoriaDuplicateException;
import com.uade.tpo.demo.exceptions.CategoriaNotFoundException;

import java.util.List;

public interface CategoriaService {
    List<Categoria> getCategorias();
    Categoria getCategoriaById(Long id) throws CategoriaNotFoundException;
    Categoria createCategoria(String nombre) throws CategoriaDuplicateException;
}