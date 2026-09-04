package com.uade.tpo.demo.controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

        // Catalogo de lectura publica; la escritura (alta/edicion/baja) queda
        // reservada al staff del cine.
        private static final String[] CATALOGO = {
                        "/productos/**", "/peliculas/**", "/funciones/**",
                        "/categorias/**", "/salas/**", "/asientos/**"
        };

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(req -> req
                                                .requestMatchers("/api/v1/auth/**").permitAll()

                                                .requestMatchers(HttpMethod.GET, CATALOGO).permitAll()
                                                .requestMatchers(HttpMethod.POST, CATALOGO).hasAuthority("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, CATALOGO).hasAuthority("ADMIN")
                                                .requestMatchers(HttpMethod.PATCH, CATALOGO).hasAuthority("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, CATALOGO).hasAuthority("ADMIN")

                                                // Panel admin: listado completo de ordenes de todos los usuarios.
                                                // GET /ordenes/{id} y PUT /ordenes/{id}/cancelar quedan autenticados
                                                // aca; la validacion de pertenencia (dueño de la orden u ADMIN) se
                                                // resuelve en OrdenServiceIMPL, no en este matcher (bloque E).
                                                .requestMatchers(HttpMethod.GET, "/ordenes").hasAuthority("ADMIN")
                                                .requestMatchers("/ordenes/**").authenticated()

                                                .requestMatchers("/api/carritos/**", "/api/checkout/**")
                                                .authenticated()

                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint()))
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
