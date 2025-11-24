package com.eduaventuras.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilidad para encriptar y verificar contraseñas usando BCrypt
 */
public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Encripta una contraseña en texto plano
     * @param passwordPlano Contraseña sin encriptar
     * @return Contraseña encriptada
     */
    public static String encriptar(String passwordPlano) {
        return encoder.encode(passwordPlano);
    }

    /**
     * Verifica si una contraseña plana coincide con una encriptada
     * @param passwordPlano Contraseña ingresada por el usuario
     * @param passwordEncriptada Contraseña guardada en la base de datos
     * @return true si coinciden, false si no
     */
    public static boolean verificar(String passwordPlano, String passwordEncriptada) {
        return encoder.matches(passwordPlano, passwordEncriptada);
    }
}