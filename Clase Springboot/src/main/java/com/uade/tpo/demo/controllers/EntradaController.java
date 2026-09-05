package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Entrada;
import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.exceptions.EntradaAccesoDenegadoException;
import com.uade.tpo.demo.exceptions.EntradaNotFoundException;
import com.uade.tpo.demo.service.EntradaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entradas")
public class EntradaController {

    @Autowired
    private EntradaService entradaService;

    // GET /entradas/mis-entradas -> Historial de entradas del usuario autenticado.
    // Declarado antes que /{id} para que la ruta literal gane frente a la variable.
    @GetMapping("/mis-entradas")
    public ResponseEntity<List<Entrada>> obtenerMisEntradas(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(entradaService.getMisEntradas(usuario));
    }

    // GET /entradas/usuario/{usuarioId} -> Entradas de un usuario (dueño o ADMIN).
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Entrada>> obtenerPorUsuario(@PathVariable Long usuarioId,
            @AuthenticationPrincipal Usuario usuario) throws EntradaAccesoDenegadoException {
        return ResponseEntity.ok(entradaService.getEntradasByUsuarioId(usuarioId, usuario));
    }

    // GET /entradas/funcion/{funcionId} -> Ocupación de butacas de una función.
    @GetMapping("/funcion/{funcionId}")
    public ResponseEntity<List<Entrada>> obtenerPorFuncion(@PathVariable Long funcionId) {
        return ResponseEntity.ok(entradaService.getEntradasByFuncion(funcionId));
    }

    // GET /entradas/{id} -> Una entrada puntual (solo el dueño o un ADMIN).
    @GetMapping("/{id}")
    public ResponseEntity<Entrada> obtenerPorId(@PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario)
            throws EntradaNotFoundException, EntradaAccesoDenegadoException {
        return ResponseEntity.ok(entradaService.getEntradaById(id, usuario));
    }
}