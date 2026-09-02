package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.Rol;
import com.uade.tpo.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor 
public class UsuarioServiceIMPL implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Usuario registrarUsuario(String username, String email, String password, String nombre, String apellido) {
        
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setEmail(email);
        
        // despues tkn
        nuevoUsuario.setPassword(password); 
        
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);


        nuevoUsuario.setRol(Rol.USER.name()); 

        return usuarioRepository.save(nuevoUsuario);
    }
}