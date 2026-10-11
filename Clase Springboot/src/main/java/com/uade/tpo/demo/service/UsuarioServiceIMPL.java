package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.dto.PerfilResponse;
import com.uade.tpo.demo.repository.EntradaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsuarioServiceIMPL implements UsuarioService {

    @Autowired
    private EntradaRepository entradaRepository;

    @Override
    public PerfilResponse getPerfil(Usuario usuario) {
        long activas = entradaRepository.countByOrdenUsuarioIdAndOrdenEstadoAndFuncionHorarioAfter(
                usuario.getId(), "CONFIRMADA", LocalDateTime.now());

        return PerfilResponse.builder()
                .nombre(nombreCompleto(usuario))
                .email(usuario.getEmail())
                // El front lo muestra en un input: null se devuelve como "".
                .telefono(usuario.getTelefono() != null ? usuario.getTelefono() : "")
                .entradasActivas((int) activas)
                .build();
    }

    // nombre + " " + apellido, sin dejar "null" si falta alguno.
    private String nombreCompleto(Usuario usuario) {
        String nombre = usuario.getNombre() != null ? usuario.getNombre() : "";
        String apellido = usuario.getApellido() != null ? usuario.getApellido() : "";
        return (nombre + " " + apellido).trim();
    }
}
