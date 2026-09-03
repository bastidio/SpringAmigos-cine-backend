package com.uade.tpo.demo.controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        // Desviacion deliberada del modelo de clase: sin un AuthenticationEntryPoint
        // propio, Spring Security responde 403 cuando falta el token en un endpoint
        // protegido (lo resuelve el filtro por defecto de autorizacion). Con este bean,
        // "sin token" y "token invalido/vencido" (ver JwtAuthenticationFilter) responden
        // los dos 401, que es lo correcto semanticamente: no estas autenticado.
        @Bean
        public AuthenticationEntryPoint authenticationEntryPoint() {
                return (request, response, authException) -> response
                                .sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autenticado");
        }

        // Reglas de autorizacion minimas para este bloque; las reglas finas por
        // metodo HTTP y recurso llegan en el bloque D.
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(req -> req.requestMatchers("/api/v1/auth/**")
                                                .permitAll()
                                                .anyRequest()
                                                .authenticated())
                                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint()))
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
