package com.Eduaventuras.config;

import com.Eduaventuras.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                        // RUTAS COMPLETAMENTE PÚBLICAS (sin token)
                        // ==========================================
                        .requestMatchers(
                                // Swagger UI
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api-docs/**",

                                // Autenticación
                                "/api/usuarios/registro",
                                "/api/usuarios/login",

                                // Gestión de contraseñas
                                "/api/password/**",

                                // Materias (lectura pública)
                                "/api/materias",
                                "/api/materias/{id}",
                                "/api/materias/todas",

                                // Recursos (lectura y descarga pública)
                                "/api/recursos",
                                "/api/recursos/{id}",
                                "/api/recursos/todos",
                                "/api/recursos/materia/{materiaId}",
                                "/api/recursos/{id}/descargar",

                                // Reportes (públicos)
                                "/api/reportes/**",

                                // Estadísticas generales (públicas)
                                "/api/estadisticas/resumen",

                                // Internacionalización
                                "/api/idioma/**"
                        ).permitAll()

                        // ==========================================
                        // RUTAS PROTEGIDAS (requieren token JWT)
                        // ==========================================

                        // Dashboard de administrador (solo ADMIN)
                        .requestMatchers("/api/admin/**").authenticated()

                        // Gestión de usuarios (autenticados)
                        .requestMatchers(
                                "/api/usuarios",          // Listar usuarios
                                "/api/usuarios/{id}",     // Ver usuario específico
                                "/api/usuarios/rol/{rol}", // Usuarios por rol
                                "/api/usuarios/estadisticas" // Estadísticas de usuarios
                        ).authenticated()

                        // Operaciones de creación/modificación (autenticados)
                        .requestMatchers(
                                "/api/materias",          // POST (crear materia)
                                "/api/materias/{id}",     // PUT/DELETE (modificar/eliminar)
                                "/api/recursos/subir"     // POST (subir recurso)
                        ).authenticated()

                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}