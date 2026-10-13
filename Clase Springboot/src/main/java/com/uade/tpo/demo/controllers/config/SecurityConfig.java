package com.uade.tpo.demo.controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        // Sin el AuthenticationEntryPoint, falta de token da 403 en vez de 401. Con este bean, sin token y token invalido responden los dos 401.
        @Bean
        public AuthenticationEntryPoint authenticationEntryPoint() {
                return (request, response, authException) -> response
                                .sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autenticado");
        }

        // get publico pero alta/edicion/baja, solo ADMIN
        private static final String[] CATALOGO = {
                        "/productos/**", "/peliculas/**", "/funciones/**",
                        "/categorias/**", "/salas/**", "/asientos/**"
        };

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .cors(Customizer.withDefaults())
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(req -> req
                                                .requestMatchers("/api/v1/auth/**").permitAll()

                                                
                                                .requestMatchers(HttpMethod.GET, "/productos/inactivos").hasAuthority("ADMIN")

                                                // /error es el dispatch interno de Tomcat y no lleva el Authorization header,
                                                // sin este permitAll los errores de negocio salian como 401 en vez de su codigo real.
                                                .requestMatchers("/error").permitAll()

                                                .requestMatchers(HttpMethod.GET, CATALOGO).permitAll()
                                                .requestMatchers(HttpMethod.POST, CATALOGO).hasAuthority("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, CATALOGO).hasAuthority("ADMIN")
                                                .requestMatchers(HttpMethod.PATCH, CATALOGO).hasAuthority("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, CATALOGO).hasAuthority("ADMIN")

                                                
                                                .requestMatchers(HttpMethod.GET, "/ordenes").hasAuthority("ADMIN")
                                                .requestMatchers("/ordenes/**").authenticated()

                                               
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
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Cambiá al puerto 5173 (o agregá ambos por las dudas)
                configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174"));

                // Agregá PATCH para que funcionen las actualizaciones de órdenes y kiosco
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

                configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
