package com.Eduaventuras.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // ========================================
        // RUTAS PÚBLICAS (SIN AUTENTICACIÓN)
        // ========================================
        if (path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars") ||
                path.equals("/api/usuarios/registro") ||
                path.equals("/api/usuarios/login") ||
                path.startsWith("/api/materias") ||
                path.startsWith("/api/recursos") ||
                path.startsWith("/api/estadisticas/resumen") ||
                path.startsWith("/api/idioma")) {

            // Estas rutas NO requieren token
            filterChain.doFilter(request, response);
            return;
        }

        // ========================================
        // RUTAS PROTEGIDAS (REQUIEREN TOKEN)
        // ========================================
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // Sin token, devolver 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token requerido. Incluye 'Authorization: Bearer {token}' en los headers\"}");
            return;
        }

        try {
            // Token presente, continuar
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token inválido: " + e.getMessage() + "\"}");
        }
    }
}

