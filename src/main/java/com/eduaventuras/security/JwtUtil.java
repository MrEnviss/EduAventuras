package com.eduaventuras.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilidad para generar y validar tokens JWT
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 horas en milisegundos
    private Long expiration;

    /**
     * Obtener la clave segura desde el secret
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

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
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Validar token JWT
     */
    public Boolean validarToken(String token, String email) {
        try {
            final String emailFromToken = obtenerEmailDelToken(token);
            return (emailFromToken.equals(email) && !esTokenExpirado(token));
        } catch (Exception e) {
            return false;
        }
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
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}