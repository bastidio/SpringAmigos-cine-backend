package com.uade.tpo.demo.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Perfil del usuario logueado (GET /users/me). Sin password.
// El rol va para que el front sepa si mostrar el panel admin (la autorizacion real sigue en SecurityConfig).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilResponse {
    private String nombre;
    private String email;
    private String telefono;
    private int entradasActivas;
    private String rol;
}
