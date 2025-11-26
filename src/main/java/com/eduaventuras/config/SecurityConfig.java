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
                        // ARCHIVOS ESTÁTICOS DEL FRONTEND (HTML, CSS, JS, IMÁGENES)
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

                                // Recursos estáticos (CSS, JS, Imágenes)
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
                                "/api/usuarios/registro",    // Registro de usuarios
                                "/api/usuarios/login"        // Login (obtener JWT)
                        ).permitAll()

                        // ==========================================
                        // GESTIÓN DE CONTRASEÑAS (PÚBLICO)
                        // ==========================================
                        .requestMatchers(
                                "/api/password/recuperar",           // Solicitar recuperación
                                "/api/password/cambiar",             // Cambiar con token
                                "/api/password/validar-token"        // Validar token de recuperación
                        ).permitAll()

                        // ==========================================
                        // MATERIAS (Lectura pública / Escritura ADMIN)
                        // ==========================================
                        .requestMatchers(HttpMethod.GET, "/api/materias/**").permitAll()        // Ver materias (público)
                        .requestMatchers(HttpMethod.POST, "/api/materias").hasAuthority("ADMIN")       // Crear materia
                        .requestMatchers(HttpMethod.PUT, "/api/materias/**").hasAuthority("ADMIN")     // Editar materia
                        .requestMatchers(HttpMethod.DELETE, "/api/materias/**").hasAuthority("ADMIN")  // Eliminar materia

                        // ==========================================
                        // RECURSOS (Lectura pública / Subida DOCENTE/ADMIN)
                        // ==========================================
                        .requestMatchers(HttpMethod.GET, "/api/recursos/**").permitAll()               // Ver/Descargar recursos (público)
                        .requestMatchers(HttpMethod.POST, "/api/recursos/subir").hasAnyAuthority("DOCENTE", "ADMIN")  // Subir PDF
                        .requestMatchers(HttpMethod.PUT, "/api/recursos/**").hasAuthority("ADMIN")     // Editar recurso
                        .requestMatchers(HttpMethod.DELETE, "/api/recursos/**").hasAnyAuthority("DOCENTE", "ADMIN")   // Eliminar recurso

                        // ==========================================
                        // PERFIL DE USUARIO (AUTENTICADO)
                        // ==========================================
                        .requestMatchers(
                                "/api/perfil/usuario",              // Obtener datos del perfil
                                "/api/perfil/actualizar",           // Actualizar datos del perfil
                                "/api/perfil/subir-foto",           // Subir foto de perfil
                                "/api/perfil/cambiar-password"      // Cambiar contraseña (autenticado)
                        ).authenticated()

                        // Fotos de perfil - Lectura pública
                        .requestMatchers(HttpMethod.GET, "/api/perfil/foto/**").permitAll()

                        // ==========================================
                        // DASHBOARD ADMINISTRATIVO (SOLO ADMIN)
                        // ==========================================
                        .requestMatchers(
                                "/api/admin/dashboard/estadisticas",     // Estadísticas del dashboard
                                "/api/admin/usuarios",                   // Listar usuarios
                                "/api/admin/usuarios/**",                // Gestionar usuarios
                                "/api/admin/reportes/**"                 // Generar reportes
                        ).hasAuthority("ADMIN")

                        // ==========================================
                        // GESTIÓN DE USUARIOS (SOLO ADMIN)
                        // ==========================================
                        .requestMatchers(
                                "/api/usuarios",                         // Listar todos los usuarios
                                "/api/usuarios/{id}",                    // Ver/Editar/Eliminar usuario específico
                                "/api/usuarios/{id}/rol",                // Cambiar rol de usuario
                                "/api/usuarios/{id}/estado"              // Activar/desactivar usuario
                        ).hasAuthority("ADMIN")

                        // ==========================================
                        // REPORTES Y ESTADÍSTICAS (PÚBLICO SEGÚN NECESIDAD)
                        // ==========================================
                        .requestMatchers(HttpMethod.GET, "/api/reportes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/estadisticas/**").permitAll()

                        // ==========================================
                        // INTERNACIONALIZACIÓN (PÚBLICO)
                        // ==========================================
                        .requestMatchers("/api/idioma/**").permitAll()

                        // ==========================================
                        // CUALQUIER OTRA RUTA REQUIERE AUTENTICACIÓN
                        // ==========================================
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}