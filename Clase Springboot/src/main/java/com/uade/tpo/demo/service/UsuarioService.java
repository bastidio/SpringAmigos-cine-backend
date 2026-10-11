package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.dto.PerfilResponse;

public interface UsuarioService {

    // Perfil de solo lectura del usuario autenticado.
    PerfilResponse getPerfil(Usuario usuario);
}
