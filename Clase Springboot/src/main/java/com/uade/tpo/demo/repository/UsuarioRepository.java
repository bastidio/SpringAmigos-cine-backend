package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // El login es por email: lo usan UserDetailsService y AuthenticationService.
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}
