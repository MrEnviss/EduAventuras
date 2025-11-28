package com.eduaventuras.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        System.out.println("🔍 [JwtFilter] " + request.getMethod() + " " + request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // Extraer token del header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                email = jwtUtil.obtenerEmailDelToken(jwt);
                System.out.println("✅ [JwtFilter] Token válido para: " + email);
            } catch (Exception e) {
                System.out.println("❌ [JwtFilter] Token inválido: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ [JwtFilter] No hay token Authorization");
        }

        // Si hay email y no hay autenticación en el contexto
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Obtener el rol del token
            String rol = jwtUtil.obtenerRolDelToken(jwt);
            System.out.println("👤 [JwtFilter] Rol del token: " + rol);

            // IMPORTANTE: Agregar prefijo "ROLE_" para que Spring Security lo reconozca
            String rolConPrefijo = "ROLE_" + rol;
            System.out.println("🔑 [JwtFilter] Rol con prefijo: " + rolConPrefijo);

            // Crear la autoridad con el prefijo correcto
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority(rolConPrefijo)
            );

            // Crear el objeto de autenticación
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Establecer en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authToken);

            System.out.println("✅ [JwtFilter] Autenticación establecida");
            System.out.println("🔐 [JwtFilter] Authorities: " + authorities);
        }

        filterChain.doFilter(request, response);
    }
}