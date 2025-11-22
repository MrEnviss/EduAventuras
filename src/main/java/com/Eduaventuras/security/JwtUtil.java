package com.Eduaventuras.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilidad para generar y validar tokens JWT
 * NOTA: Por ahora está creado pero no se usa (para futuras implementaciones)
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:EduAventuras2024SecretKeySuperSeguraParaJWT}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 horas en milisegundos
    private Long expiration;

    /**
     * Generar token JWT para un usuario
     */
    public String generarToken(String email, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * Validar token JWT
     */
    public Boolean validarToken(String token, String email) {
        final String emailFromToken = obtenerEmailDelToken(token);
        return (emailFromToken.equals(email) && !esTokenExpirado(token));
    }

    /**
     * Obtener email del token
     */
    public String obtenerEmailDelToken(String token) {
        return obtenerClaimsDelToken(token).getSubject();
    }

    /**
     * Obtener rol del token
     */
    public String obtenerRolDelToken(String token) {
        return (String) obtenerClaimsDelToken(token).get("rol");
    }

    /**
     * Verificar si el token está expirado
     */
    private Boolean esTokenExpirado(String token) {
        final Date fechaExpiracion = obtenerClaimsDelToken(token).getExpiration();
        return fechaExpiracion.before(new Date());
    }

    /**
     * Obtener claims del token
     */
    private Claims obtenerClaimsDelToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}