package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.dto.PerfilResponse;
import com.uade.tpo.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // GET /users/me -> Perfil del usuario autenticado (sale del JWT, no del URL)
    @GetMapping("/me")
    public ResponseEntity<PerfilResponse> obtenerPerfil(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(usuarioService.getPerfil(usuario));
    }
}
