package com.uade.tpo.demo.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Builder
// @NoArgsConstructor y @AllArgsConstructor son obligatorios al usar @Builder en una
// entidad JPA: Hibernate necesita el constructor vacio y @Builder lo elimina.
// Es el mismo error que rompio POST /productos con Categoria en la Etapa 1.
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dato de perfil, NO se usa para autenticar. El campo se llama nombreUsuario
    // y no username porque getUsername() esta reservado por UserDetails y tiene
    // que devolver el email, que es nuestro identificador de login.
    @Column(name = "username")
    private String nombreUsuario;

    @Column(unique = true)
    private String email;

    @Column
    @JsonIgnore
    private String password;

    @Column
    private String nombre;

    @Column
    private String apellido;

    @Enumerated(EnumType.STRING)
    @Column
    private Rol rol;

    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Orden> orders;

    // Desviacion deliberada del modelo de clase: @JsonIgnore en los seis getters
    // heredados de UserDetails de aca para abajo. Sin esto, Jackson los serializa
    // como si fueran propiedades del bean (authorities, accountNonExpired, etc.)
    // y el JSON de cualquier Usuario -incluido el que viaja dentro de Orden- se
    // ensucia con datos de autenticacion que no son del dominio.

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.name()));
    }

    // El identificador de login es el email (ver UsuarioRepository.findByEmail).
    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
