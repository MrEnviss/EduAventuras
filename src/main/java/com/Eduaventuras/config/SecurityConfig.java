package com.Eduaventuras.config;

import com.Eduaventuras.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ==========================================
                        // RUTAS COMPLETAMENTE PUBLICAS (sin token)
                        // ==========================================
                        .requestMatchers(
                                // Swagger UI
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api-docs/**",

                                // Autenticacion
                                "/api/usuarios/registro",
                                "/api/usuarios/login",

                                // Gestion de contraseñas
                                "/api/password/**",

                                // Fotos de perfil (lectura publica)
                                "/api/perfil/foto/{usuarioId}"
                        ).permitAll()

                        // Materias - Solo lectura publica
                        .requestMatchers(HttpMethod.GET, "/api/materias", "/api/materias/**").permitAll()

                        // Recursos - Solo lectura y descarga publica
                        .requestMatchers(HttpMethod.GET,
                                "/api/recursos",
                                "/api/recursos/**"
                        ).permitAll()

                        // Reportes - Publicos
                        .requestMatchers(HttpMethod.GET, "/api/reportes/**").permitAll()

                        // Estadisticas generales - Publicas
                        .requestMatchers(HttpMethod.GET, "/api/estadisticas/resumen").permitAll()

                        // Internacionalizacion - Publico
                        .requestMatchers("/api/idioma/**").permitAll()

                        // ==========================================
                        // RUTAS PROTEGIDAS CON AUTENTICACION
                        // ==========================================

                        // Perfil - Cualquier usuario autenticado
                        .requestMatchers("/api/perfil", "/api/perfil/**").authenticated()

                        // Dashboard - Solo ADMIN
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                        // Gestion de usuarios - Solo ADMIN
                        .requestMatchers("/api/usuarios", "/api/usuarios/**").hasAuthority("ADMIN")

                        // Materias - Crear/Editar/Eliminar solo ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/materias").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/materias/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/materias/**").hasAuthority("ADMIN")

                        // Recursos - Subir: DOCENTE o ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/recursos/subir").hasAnyAuthority("DOCENTE", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/recursos/**").hasAnyAuthority("DOCENTE", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/recursos/**").hasAuthority("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}