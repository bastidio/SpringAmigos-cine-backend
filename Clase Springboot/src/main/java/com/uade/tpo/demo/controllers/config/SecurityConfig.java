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

                                                // Cuando un controller lanza una excepcion con @ResponseStatus,
                                                // Spring hace response.sendError(...) y el servlet re-despacha a
                                                // /error. Ese re-despacho vuelve a pasar por la cadena de seguridad
                                                // pero SIN el JwtAuthenticationFilter (OncePerRequestFilter no corre
                                                // en ERROR dispatch), asi que sin este permitAll un 404/403 legitimo
                                                // termina respondiendo 401.
                                                .requestMatchers("/error").permitAll()

                                                // El listado de productos dados de baja es solo para el staff.
                                                // Va ANTES del GET publico de CATALOGO: si no, /productos/**
                                                // lo agarraria primero y lo dejaria abierto.
                                                .requestMatchers(HttpMethod.GET, "/productos/inactivos").hasAuthority("ADMIN")

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

                                                // Ocupacion de una funcion: publica, igual que el catalogo, y
                                                // solo expone ids de asiento (OcupacionFuncionResponse). El resto
                                                // de /entradas/** (mis-entradas, por usuario, por id) es privado
                                                // y ademas valida pertenencia en EntradaServiceIMPL.
                                                .requestMatchers(HttpMethod.GET, "/entradas/funcion/**").permitAll()
                                                .requestMatchers("/entradas/**").authenticated()

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
