package com.Eduaventuras.util;

import com.Eduaventuras.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Utilidad para obtener informacion del usuario autenticado desde el token JWT
 */
@Component
public class AuthUtil {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Obtener el email del usuario logueado desde el token JWT
     */
    public String obtenerEmailDelToken() {
        try {
            HttpServletRequest request = obtenerRequest();
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return jwtUtil.obtenerEmailDelToken(token);
            }

            throw new RuntimeException("Token no encontrado");
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener email del token: " + e.getMessage());
        }
    }

    /**
     * Obtener el rol del usuario logueado desde el token JWT
     */
    public String obtenerRolDelToken() {
        try {
            HttpServletRequest request = obtenerRequest();
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return jwtUtil.obtenerRolDelToken(token);
            }

            throw new RuntimeException("Token no encontrado");
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener rol del token: " + e.getMessage());
        }
    }

    /**
     * Verificar si el usuario tiene un rol especifico
     */
    public boolean tieneRol(String rolRequerido) {
        String rolActual = obtenerRolDelToken();
        return rolActual.equals(rolRequerido);
    }

    /**
     * Verificar si el usuario es ADMIN
     */
    public boolean esAdmin() {
        return tieneRol("ADMIN");
    }

    /**
     * Verificar si el usuario es DOCENTE o ADMIN
     */
    public boolean esDocenteOAdmin() {
        String rol = obtenerRolDelToken();
        return rol.equals("DOCENTE") || rol.equals("ADMIN");
    }

    /**
     * Obtener el request actual
     */
    private HttpServletRequest obtenerRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new RuntimeException("No hay request en el contexto");
        }

        return attributes.getRequest();
    }
}