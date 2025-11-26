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
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // Lista de rutas públicas que NO requieren token
    private static final List<String> RUTAS_PUBLICAS = List.of(
            // Archivos HTML del frontend
            "/",
            "/home.html",
            "/login.html",
            "/registro.html",
            "/recuperar-password.html",
            "/materias.html",
            "/recursos.html",
            "/404.html",

            // Recursos estáticos
            "/assets/",
            "/css/",
            "/js/",
            "/images/",
            "/icons/",
            "/favicon.ico",

            // Swagger
            "/swagger-ui",
            "/v3/api-docs",
            "/api-docs",
            "/swagger-resources",
            "/webjars",

            // API - Autenticación
            "/api/usuarios/registro",
            "/api/usuarios/login",

            // API - Gestión de contraseñas
            "/api/password",

            // API - Internacionalización
            "/api/idioma",

            // API - Fotos de perfil (lectura)
            "/api/perfil/foto/",

            // API - Estadísticas públicas
            "/api/estadisticas",
            "/api/reportes"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();



        // ==========================================
        // VERIFICAR SI ES UNA RUTA PÚBLICA
        // ==========================================
        if (esRutaPublica(path, method)) {

            filterChain.doFilter(request, response);
            return;
        }

        // ==========================================
        // RUTAS PROTEGIDAS: REQUIEREN TOKEN JWT
        // ==========================================
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("🔑 Header Authorization: " + (authorizationHeader != null ? "PRESENTE" : "❌ AUSENTE"));

        // Verificar que el header existe y tiene el formato correcto
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.err.println("❌ Token JWT no encontrado o formato incorrecto");
            enviarErrorNoAutorizado(response, "Token requerido. Incluye 'Authorization: Bearer {token}' en los headers");
            return;
        }

        try {
            // Extraer el token (quitar "Bearer ")
            String token = authorizationHeader.substring(7);
            System.out.println("🎫 Token recibido: " + token.substring(0, Math.min(20, token.length())) + "...");

            // Extraer información del token
            String email = jwtUtil.obtenerEmailDelToken(token);
            String rol = jwtUtil.obtenerRolDelToken(token);

            System.out.println("👤 Email extraído: " + email);
            System.out.println("🏷️  Rol extraído: " + rol);

            // Validar el token
            if (jwtUtil.validarToken(token, email)) {
                System.out.println("✅ Token VÁLIDO");

                // Crear autoridad (rol del usuario)
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rol);
                System.out.println("🔐 Autoridad asignada: " + authority.getAuthority());

                // Crear autenticación en el contexto de seguridad
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                Collections.singletonList(authority)
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("✅ Usuario autenticado en el contexto de seguridad");

                // Continuar con la petición
                filterChain.doFilter(request, response);

            } else {
                System.err.println("❌ Token INVÁLIDO o EXPIRADO");
                enviarErrorNoAutorizado(response, "Token inválido o expirado");
            }

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.err.println("❌ Token EXPIRADO: " + e.getMessage());
            enviarErrorNoAutorizado(response, "El token ha expirado. Por favor, inicia sesión nuevamente");

        } catch (io.jsonwebtoken.MalformedJwtException e) {
            System.err.println("❌ Token MAL FORMADO: " + e.getMessage());
            enviarErrorNoAutorizado(response, "Token inválido. Formato incorrecto");

        } catch (io.jsonwebtoken.SignatureException e) {
            System.err.println("❌ FIRMA INVÁLIDA: " + e.getMessage());
            enviarErrorNoAutorizado(response, "Token inválido. Firma no válida");

        } catch (Exception e) {
            System.err.println("❌ EXCEPCIÓN en JwtFilter: " + e.getClass().getName());
            System.err.println("📄 Mensaje: " + e.getMessage());
            e.printStackTrace();
            enviarErrorNoAutorizado(response, "Error al validar token: " + e.getMessage());
        }
    }

    /**
     * Verifica si una ruta es pública (no requiere token)
     */
    private boolean esRutaPublica(String path, String method) {
        // Verificar rutas exactas
        for (String rutaPublica : RUTAS_PUBLICAS) {
            if (path.equals(rutaPublica) || path.startsWith(rutaPublica)) {
                return true;
            }
        }

        // Casos especiales: GET en materias y recursos (público)
        if (method.equals("GET") && (path.startsWith("/api/materias") || path.startsWith("/api/recursos"))) {
            return true;
        }

        // Casos especiales: GET en reportes y estadísticas (público)
        if (method.equals("GET") && (path.startsWith("/api/reportes") || path.startsWith("/api/estadisticas"))) {
            return true;
        }

        return false;
    }

    /**
     * Envía respuesta de error 401 Unauthorized en formato JSON
     */
    private void enviarErrorNoAutorizado(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format(
                "{\"error\": \"%s\", \"status\": 401, \"timestamp\": \"%s\"}",
                mensaje,
                java.time.LocalDateTime.now().toString()
        ));
    }
}