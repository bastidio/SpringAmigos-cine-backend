package com.uade.tpo.demo.controllers.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Sin campo rol: el registro siempre crea usuarios con rol USER (ver
// AuthenticationService.register). Los ADMIN se crean a mano en la base.
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
}
