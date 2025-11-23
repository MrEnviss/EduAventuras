package com.Eduaventuras.controller;

import com.Eduaventuras.dto.ActualizarPerfilDTO;
import com.Eduaventuras.dto.UsuarioDTO;
import com.Eduaventuras.service.UsuarioService;
import com.Eduaventuras.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/perfil")
@CrossOrigin(origins = "*")
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthUtil authUtil;

    /**
     * GET /api/perfil
     * Ver mi perfil (usuario logueado)
     */
    @GetMapping
    public ResponseEntity<?> verMiPerfil() {
        try {
            String email = authUtil.obtenerEmailDelToken();
            UsuarioDTO usuario = usuarioService.buscarPorEmail(email);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error al obtener perfil: " + e.getMessage()
            ));
        }
    }

    /**
     * PUT /api/perfil
     * Actualizar mi perfil (nombre, apellido)
     */
    @PutMapping
    public ResponseEntity<?> actualizarMiPerfil(@Valid @RequestBody ActualizarPerfilDTO dto) {
        try {
            String email = authUtil.obtenerEmailDelToken();
            UsuarioDTO usuario = usuarioService.actualizarPerfil(email, dto);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Perfil actualizado exitosamente",
                    "usuario", usuario
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * POST /api/perfil/foto
     * Subir foto de perfil
     */
    @PostMapping(value = "/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirFotoPerfil(@RequestParam("foto") MultipartFile foto) {
        try {
            String email = authUtil.obtenerEmailDelToken();
            String rutaFoto = usuarioService.subirFotoPerfil(email, foto);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Foto de perfil actualizada exitosamente",
                    "rutaFoto", rutaFoto
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * GET /api/perfil/foto/{usuarioId}
     * Obtener foto de perfil de cualquier usuario (público)
     */
    @GetMapping("/foto/{usuarioId}")
    public ResponseEntity<?> obtenerFotoPerfil(@PathVariable Long usuarioId) {
        try {
            byte[] foto = usuarioService.obtenerFotoPerfil(usuarioId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(foto);
        } catch (Exception e) {
            // Devolver imagen por defecto si no tiene foto
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/perfil/foto
     * Eliminar mi foto de perfil
     */
    @DeleteMapping("/foto")
    public ResponseEntity<?> eliminarFotoPerfil() {
        try {
            String email = authUtil.obtenerEmailDelToken();
            usuarioService.eliminarFotoPerfil(email);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Foto de perfil eliminada exitosamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}