package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Usuario;

public interface UsuarioService {
    Usuario registrarUsuario(String username, String email, String password, String nombre, String apellido);
}