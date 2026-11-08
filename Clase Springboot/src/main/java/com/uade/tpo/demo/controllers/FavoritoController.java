package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.dto.FavoritoResponse;
import com.uade.tpo.demo.exceptions.PeliculaNotFoundException;
import com.uade.tpo.demo.service.FavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Autenticado por el anyRequest() de SecurityConfig; el usuario sale siempre del token.
@RestController
@RequestMapping("/favoritos")
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    @GetMapping
    public ResponseEntity<List<FavoritoResponse>> obtenerMisFavoritos(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(favoritoService.getMisFavoritos(usuario));
    }

    @PostMapping("/{peliculaId}")
    public ResponseEntity<Void> agregar(@PathVariable Long peliculaId,
            @AuthenticationPrincipal Usuario usuario) throws PeliculaNotFoundException {
        favoritoService.agregar(usuario, peliculaId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{peliculaId}")
    public ResponseEntity<Void> quitar(@PathVariable Long peliculaId, @AuthenticationPrincipal Usuario usuario) {
        favoritoService.quitar(usuario, peliculaId);
        return ResponseEntity.noContent().build();
    }
}
