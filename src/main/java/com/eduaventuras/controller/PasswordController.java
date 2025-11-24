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
    public ResponseEntity<?> cambiarPassword(@Valid @RequestBody CambiarPasswordDTO dto) {
        try {
            usuarioService.cambiarPassword(dto.getEmail(), dto.getPasswordActual(), dto.getPasswordNueva());
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
     * POST /api/password/solicitar-recuperacion
     * Solicitar recuperación de contraseña (recibe email)
     * NOTA: Por ahora solo valida que el email exista
     * En producción enviaría un email con token
     */
    @PostMapping("/solicitar-recuperacion")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");

            // Verificar que el email existe
            usuarioService.verificarEmailExiste(email);

            // TODO: En producción, generar token y enviar email
            // Por ahora solo generamos un token temporal
            String tokenTemporal = usuarioService.generarTokenRecuperacion(email);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Si el email existe, recibirás instrucciones para recuperar tu contraseña",
                    "token_temporal", tokenTemporal, // Solo para desarrollo, ELIMINAR en producción
                    "nota", "En producción este token se enviará por email"
            ));
        } catch (Exception e) {
            // Por seguridad, siempre devolver el mismo mensaje
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Si el email existe, recibirás instrucciones para recuperar tu contraseña"
            ));
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
            String nuevaPassword = request.get("nuevaPassword");

            usuarioService.restablecerPasswordConToken(token, nuevaPassword);

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