package com.eduaventuras.config;

import com.eduaventuras.security.JwtFilter;
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
                                "/api-docs/**"
                        ).permitAll()

                        // Autenticacion
                        .requestMatchers(
                                "/api/usuarios/registro",
                                "/api/usuarios/login"
                        ).permitAll()

                        // Gestion de contraseñas
                        .requestMatchers("/api/password/**").permitAll()

                        // Materias - Solo lectura publica
                        .requestMatchers(HttpMethod.GET, "/api/materias/**").permitAll()

                        // Recursos - Solo lectura y descarga publica
                        .requestMatchers(HttpMethod.GET, "/api/recursos/**").permitAll()

                        // Reportes - Publicos
                        .requestMatchers(HttpMethod.GET, "/api/reportes/**").permitAll()

                        // Estadisticas generales - Publicas
                        .requestMatchers(HttpMethod.GET, "/api/estadisticas/**").permitAll()

                        // Fotos de perfil - Lectura publica
                        .requestMatchers(HttpMethod.GET, "/api/perfil/foto/**").permitAll()

                        // Internacionalizacion - Publico
                        .requestMatchers("/api/idioma/**").permitAll()

                        // ==========================================
                        // RUTAS PROTEGIDAS CON AUTENTICACION
                        // ==========================================

                        // Dashboard
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                        // Perfil
                        .requestMatchers("/api/perfil/**").authenticated()

                        // Gestion de usuarios
                        .requestMatchers("/api/usuarios/**").hasAuthority("ADMIN")

                        // Materias - Crear/Editar/Eliminar
                        .requestMatchers(HttpMethod.POST, "/api/materias/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/materias/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/materias/**").hasAuthority("ADMIN")

                        // Recursos - Subir
                        .requestMatchers(HttpMethod.POST, "/api/recursos/subir").hasAnyAuthority("DOCENTE", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/recursos/**").hasAnyAuthority("DOCENTE", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/recursos/**").hasAuthority("ADMIN")

                        // Cualquier otra ruta requiere autenticacion
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}