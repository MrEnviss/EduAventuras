package com.Eduaventuras.util;

import com.Eduaventuras.model.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utilidad para obtener el usuario autenticado desde el token JWT
 */
public class AuthUtil {

    /**
     * Obtener el usuario autenticado actualmente
     * @return Usuario autenticado o null si no hay autenticación
     */
    public static Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
            return (Usuario) authentication.getPrincipal();
        }

        return null;
    }

    /**
     * Obtener el ID del usuario autenticado
     * @return ID del usuario o null si no está autenticado
     */
    public static Long obtenerUsuarioId() {
        Usuario usuario = obtenerUsuarioAutenticado();
        return usuario != null ? usuario.getId() : null;
    }

    /**
     * Obtener el email del usuario autenticado
     * @return Email del usuario o null si no está autenticado
     */
    public static String obtenerUsuarioEmail() {
        Usuario usuario = obtenerUsuarioAutenticado();
        return usuario != null ? usuario.getEmail() : null;
    }

    /**
     * Verificar si hay un usuario autenticado
     * @return true si hay usuario autenticado, false si no
     */
    public static boolean estaAutenticado() {
        return obtenerUsuarioAutenticado() != null;
    }
}