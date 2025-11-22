package com.Eduaventuras.security;

import com.Eduaventuras.model.Usuario;
import com.Eduaventuras.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Filtro JWT para validar tokens en cada petición
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // Extraer token del header "Authorization: Bearer {token}"
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                email = jwtUtil.obtenerEmailDelToken(jwt);
            } catch (Exception e) {
                System.err.println("❌ Token inválido: " + e.getMessage());
            }
        }

        // Si el token es válido, autenticar al usuario
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

            if (usuario != null && jwtUtil.validarToken(jwt, email)) {

                // Crear autenticación de Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(usuario, null, new ArrayList<>());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Guardar autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("✅ Usuario autenticado: " + email + " (Rol: " + usuario.getRol() + ")");
            }
        }

        filterChain.doFilter(request, response);
    }
}