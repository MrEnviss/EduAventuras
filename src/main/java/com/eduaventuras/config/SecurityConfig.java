package com.eduaventuras.config;

import com.eduaventuras.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
                .authorizeHttpRequests(auth -> {

                    // ==========================================
                    // 🔍 DEBUG: Ver qué usuario está en el contexto
                    // ==========================================
                    Authentication authContext = SecurityContextHolder.getContext().getAuthentication();
                    if (authContext != null) {
                        System.out.println("🔐 [SecurityConfig] Usuario en contexto: " + authContext.getName());
                        System.out.println("🔐 [SecurityConfig] Authorities: " + authContext.getAuthorities());
                    } else {
                        System.out.println("⚠️ [SecurityConfig] NO HAY usuario en el contexto");
                    }

                    auth
                            // ==========================================
                            // ARCHIVOS ESTÁTICOS DEL FRONTEND
                            // ==========================================
                            .requestMatchers(
                                    "/",
                                    "/home.html",
                                    "/login.html",
                                    "/registro.html",
                                    "/recuperar-password.html",
                                    "/materias.html",
                                    "/recursos.html",
                                    "/perfil.html",
                                    "/editar-perfil.html",
                                    "/cambiar-password.html",
                                    "/admin-dashboard.html",
                                    "/admin-materias.html",
                                    "/admin-recursos.html",
                                    "/admin-usuarios.html",
                                    "/subir-recurso.html",
                                    "/404.html",
                                    "/assets/**",
                                    "/css/**",
                                    "/js/**",
                                    "/images/**",
                                    "/icons/**",
                                    "/favicon.ico"
                            ).permitAll()

                            // ==========================================
                            // SWAGGER / API DOCS
                            // ==========================================
                            .requestMatchers(
                                    "/swagger-ui.html",
                                    "/swagger-ui/**",
                                    "/v3/api-docs/**",
                                    "/swagger-resources/**",
                                    "/webjars/**",
                                    "/api-docs/**"
                            ).permitAll()

                            // ==========================================
                            // AUTENTICACIÓN (PÚBLICO)
                            // ==========================================
                            .requestMatchers(
                                    "/api/usuarios/registro",
                                    "/api/usuarios/login"
                            ).permitAll()

                            // ==========================================
                            // GESTIÓN DE CONTRASEÑAS (PÚBLICO)
                            // ==========================================
                            .requestMatchers(
                                    "/api/password/recuperar",
                                    "/api/password/cambiar",
                                    "/api/password/validar-token"
                            ).permitAll()

                            // ==========================================
                            // 🔴 RUTAS DE ADMIN - DEBEN IR ANTES
                            // ==========================================
                            .requestMatchers("/api/admin/**").hasRole("ADMIN")
                            .requestMatchers("/api/usuarios").hasRole("ADMIN")
                            .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                            // ==========================================
                            // MATERIAS
                            // ==========================================
                            .requestMatchers(HttpMethod.GET, "/api/materias/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/materias").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/materias/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/materias/**").hasRole("ADMIN")

                            // ==========================================
                            // RECURSOS
                            // ==========================================
                            .requestMatchers(HttpMethod.GET, "/api/recursos/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/recursos/subir").hasAnyRole("DOCENTE", "ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/recursos/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/recursos/**").hasAnyRole("DOCENTE", "ADMIN")

                            // ==========================================
                            // PERFIL DE USUARIO (AUTENTICADO)
                            // ==========================================
                            .requestMatchers(
                                    "/api/perfil/usuario",
                                    "/api/perfil/actualizar",
                                    "/api/perfil/subir-foto",
                                    "/api/perfil/cambiar-password"
                            ).authenticated()

                            .requestMatchers(HttpMethod.GET, "/api/perfil/foto/**").permitAll()

                            // ==========================================
                            // INTERNACIONALIZACIÓN (PÚBLICO)
                            // ==========================================
                            .requestMatchers("/api/idioma/**").permitAll()

                            // ==========================================
                            // CUALQUIER OTRA RUTA REQUIERE AUTENTICACIÓN
                            // ==========================================
                            .anyRequest().authenticated();
                })
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}