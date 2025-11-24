package com.eduaventuras.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();



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
                path.startsWith("/api/password") ||
                path.startsWith("/api/idioma") ||
                path.startsWith("/api/perfil/foto/") ||
                path.equals("/api/estadisticas/resumen") ||
                (path.startsWith("/api/materias") && method.equals("GET")) ||
                (path.startsWith("/api/recursos") && method.equals("GET")) ||
                (path.startsWith("/api/reportes") && method.equals("GET"))) {


            filterChain.doFilter(request, response);
            return;
        }

        // ========================================
        // RUTAS PROTEGIDAS (REQUIEREN TOKEN)
        // ========================================


        String authorizationHeader = request.getHeader("Authorization");
        System.out.println(" Header Authorization: " + (authorizationHeader != null ? "PRESENTE" : "AUSENTE"));

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                       response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token requerido. Incluye 'Authorization: Bearer {token}' en los headers\"}");
            return;
        }

        try {
            String token = authorizationHeader.substring(7);
            System.out.println(" Token recibido: " + token.substring(0, Math.min(20, token.length())) + "...");

            String email = jwtUtil.obtenerEmailDelToken(token);
            String rol = jwtUtil.obtenerRolDelToken(token);

            System.out.println(" Email extraído: " + email);
            System.out.println(" Rol extraído: " + rol);

            if (jwtUtil.validarToken(token, email)) {
                System.out.println(" Token VÁLIDO");

                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rol);
                System.out.println(" Autoridad creada: " + authority.getAuthority());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                Collections.singletonList(authority)
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);


                filterChain.doFilter(request, response);

            } else {

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token inválido o expirado\"}");
            }

        } catch (Exception e) {
            System.err.println(" EXCEPCIÓN en JwtFilter: " + e.getClass().getName());
            System.err.println(" Mensaje: " + e.getMessage());
            e.printStackTrace();


            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Error al validar token: " + e.getMessage() + "\"}");
        }
    }
}