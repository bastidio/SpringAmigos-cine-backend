package com.uade.tpo.demo.controllers.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Sin campo rol a proposito: el registro siempre crea USER, para que sea ADMIN hay que hacer update desde la BD.
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String username;
    private String email;
    private String password;
    private String nombre;
    private String apellido;
    // Opcional.
    private String telefono;
}
