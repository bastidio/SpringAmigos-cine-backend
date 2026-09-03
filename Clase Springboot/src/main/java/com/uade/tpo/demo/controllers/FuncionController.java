package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Funcion;
import com.uade.tpo.demo.entity.dto.FuncionRequest;
import com.uade.tpo.demo.exceptions.FuncionDuplicateException;
import com.uade.tpo.demo.exceptions.FuncionNotFoundException;
import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;
import com.uade.tpo.demo.exceptions.SalaNotFoundException;
import com.uade.tpo.demo.service.FuncionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/funciones")
public class FuncionController {

    @Autowired
    private FuncionService funcionService;

    @GetMapping
    public ResponseEntity<List<Funcion>> getFunciones() {
        return ResponseEntity.ok(funcionService.getFunciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcion> getFuncionById(@PathVariable Long id) throws FuncionNotFoundException {
        Funcion funcion = funcionService.getFuncionById(id);
        return ResponseEntity.ok(funcion);
    }

    @PostMapping
    public ResponseEntity<Funcion> createFuncion(@RequestBody FuncionRequest request)
            throws PeliculaNotFoundException, SalaNotFoundException, FuncionDuplicateException {
        Funcion nuevaFuncion = funcionService.createFuncion(
                request.getPeliculaId(),
                request.getSalaId(),
                request.getHorario(),
                request.getIdioma(),
                request.getFormato(),
                request.getPrecio_base()
        );
        return ResponseEntity.created(URI.create("/funciones/" + nuevaFuncion.getId())).body(nuevaFuncion);
    }
}