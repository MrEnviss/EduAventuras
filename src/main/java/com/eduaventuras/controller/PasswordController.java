package com.eduaventuras.controller;

import com.eduaventuras.dto.CambiarPasswordDTO;
import com.eduaventuras.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/password")
@CrossOrigin(origins = "*")
public class PasswordController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * POST /api/password/cambiar
     * Cambiar contraseña (usuario logueado)
     */
    @PostMapping("/cambiar")
    public ResponseEntity<?> cambiarPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String passwordActual = request.get("passwordActual");
            String passwordNueva = request.get("passwordNueva");

            usuarioService.cambiarPassword(email, passwordActual, passwordNueva);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Contraseña actualizada exitosamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * POST /api/password/recuperar
     * Solicitar recuperación de contraseña (genera token)
     */
    @PostMapping("/recuperar")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El email es requerido"));
            }

            // Verificar que el email existe (sin revelar si existe o no)
            String token = null;
            try {
                usuarioService.verificarEmailExiste(email);
                token = usuarioService.generarTokenRecuperacion(email);
                System.out.println("🔑 Token generado para: " + email);
            } catch (Exception e) {
                // Por seguridad, no revelar si el email existe
            }

            // Respuesta genérica (por seguridad)
            Map<String, Object> response = Map.of(
                    "mensaje", "Si el email existe, recibirás un enlace de recuperación"
            );

            // Solo para testing - EN PRODUCCIÓN ELIMINAR ESTO
            if (token != null) {
                String enlace = "http://localhost:8080/recuperar-password.html?token=" + token;
                return ResponseEntity.ok(Map.of(
                        "mensaje", "Si el email existe, recibirás un enlace de recuperación",
                        "token", token,  // Solo para testing
                        "enlace", enlace // Solo para testing
                ));
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Por seguridad, siempre devolver el mismo mensaje
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Si el email existe, recibirás un enlace de recuperación"
            ));
        }
    }

    /**
     * POST /api/password/validar-token
     * Validar que el token de recuperación es válido
     */
    @PostMapping("/validar-token")
    public ResponseEntity<?> validarToken(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");

            if (token == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Token requerido"));
            }

            // Validar token usando el método del servicio
            boolean valido = usuarioService.validarTokenRecuperacion(token);

            if (valido) {
                String email = usuarioService.obtenerEmailPorToken(token);
                return ResponseEntity.ok(Map.of(
                        "valido", true,
                        "email", email
                ));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Token inválido o expirado"));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Token inválido o expirado"));
        }
    }

    /**
     * POST /api/password/restablecer
     * Restablecer contraseña con token de recuperación
     */
    @PostMapping("/restablecer")
    public ResponseEntity<?> restablecerPassword(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            String password = request.get("password");
            String nuevaPassword = request.get("nuevaPassword");

            // Aceptar tanto 'password' como 'nuevaPassword'
            String passwordFinal = password != null ? password : nuevaPassword;

            if (token == null || passwordFinal == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Token y contraseña son requeridos"));
            }

            usuarioService.restablecerPasswordConToken(token, passwordFinal);

            System.out.println("✅ Contraseña restablecida exitosamente");

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Contraseña restablecida exitosamente. Ya puedes iniciar sesión."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}