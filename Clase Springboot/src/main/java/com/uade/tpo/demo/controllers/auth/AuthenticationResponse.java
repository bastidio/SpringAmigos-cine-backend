package com.uade.tpo.demo.controllers.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    @JsonProperty("access_token")
    private String accessToken;

    // "ADMIN" o "USER", para que el front no tenga que pedir /users/me despues del login.
    private String rol;

    // Lo que usa el avatar del nav, asi se ve la foto sin pedir el perfil despues del login.
    private String nombre;
    private String fotoPerfil;
}
