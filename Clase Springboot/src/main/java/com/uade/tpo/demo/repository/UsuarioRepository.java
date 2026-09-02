package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Este método nos va a servir más adelante para el Login con JWT, cuando se haga borrenme 
    Optional<Usuario> findByUsername(String username);
}