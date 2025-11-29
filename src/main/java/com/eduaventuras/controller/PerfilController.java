package com.eduaventuras.controller;

import com.eduaventuras.dto.ActualizarPerfilDTO;
import com.eduaventuras.dto.UsuarioDTO;
import com.eduaventuras.service.UsuarioService;
import com.eduaventuras.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/perfil")
@CrossOrigin(origins = "*")
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthUtil authUtil;

    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;

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
            System.out.println("📤 Iniciando carga de foto...");

            // Obtener email del usuario autenticado
            String email = authUtil.obtenerEmailDelToken();
            System.out.println("👤 Email del usuario: " + email);

            // Validar archivo
            if (foto.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "El archivo está vacío"
                ));
            }

            String contentType = foto.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Solo se permiten imágenes"
                ));
            }

            // Validar tamaño (máximo 5MB)
            if (foto.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "La imagen es demasiado grande (máximo 5MB)"
                ));
            }

            System.out.println("📁 Upload dir: " + uploadDir);

            // Crear directorio si no existe
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("✅ Directorio creado: " + uploadPath.toAbsolutePath());
            }

            // Generar nombre único para la foto
            String extension = getExtensionFromContentType(contentType);
            String nombreArchivo = email.replace("@", "_").replace(".", "_") + "_" + UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(nombreArchivo);

            // Guardar archivo
            Files.write(filePath, foto.getBytes());
            System.out.println("✅ Archivo guardado en: " + filePath.toAbsolutePath());

            // Actualizar usuario con la ruta de la foto
            String rutaFoto = "/uploads/" + nombreArchivo;
            usuarioService.actualizarFotoPerfil(email, rutaFoto);
            System.out.println("✅ Ruta de foto actualizada: " + rutaFoto);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Foto de perfil actualizada exitosamente",
                    "foto", rutaFoto
            ));

        } catch (IOException e) {
            System.err.println("❌ Error de I/O: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Error al guardar la foto: " + e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("❌ Error general: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Error al subir foto: " + e.getMessage()
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
            System.out.println("📸 Buscando foto del usuario: " + usuarioId);

            // Obtener la ruta de la foto del usuario
            String rutaFoto = usuarioService.obtenerRutaFotoPerfil(usuarioId);

            if (rutaFoto == null || rutaFoto.isEmpty()) {
                System.out.println("⚠️ Usuario sin foto");
                return ResponseEntity.notFound().build();
            }

            // Limpiar la ruta (remover /uploads/ si está incluido)
            String nombreArchivo = rutaFoto.replace("/uploads/", "").replace("uploads/", "");

            System.out.println("📁 Nombre del archivo: " + nombreArchivo);
            System.out.println("📁 Upload dir: " + uploadDir);

            Path filePath = Paths.get(uploadDir).resolve(nombreArchivo);

            System.out.println("📁 Buscando archivo en: " + filePath.toAbsolutePath());

            if (!Files.exists(filePath)) {
                System.out.println("❌ Archivo no encontrado en: " + filePath.toAbsolutePath());

                // Intentar buscar en carpeta relativa
                Path altPath = Paths.get("src/main/resources/static/uploads/").resolve(nombreArchivo);
                System.out.println("🔄 Intentando ruta alternativa: " + altPath.toAbsolutePath());

                if (Files.exists(altPath)) {
                    filePath = altPath;
                    System.out.println("✅ Archivo encontrado en ruta alternativa");
                } else {
                    System.out.println("❌ Archivo tampoco existe en ruta alternativa");
                    return ResponseEntity.notFound().build();
                }
            }

            // Leer el archivo
            Resource resource = new UrlResource(filePath.toUri());
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "image/png";
            }

            System.out.println("✅ Sirviendo foto: " + filePath.getFileName());
            System.out.println("📊 Content-Type: " + contentType);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath.getFileName() + "\"")
                    .header("Cache-Control", "no-cache, must-revalidate")
                    .body(resource);

        } catch (MalformedURLException e) {
            System.err.println("❌ Error de URL: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Error al procesar la foto"
            ));
        } catch (IOException e) {
            System.err.println("❌ Error de I/O: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Error al leer la foto"
            ));
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

            // Obtener la ruta actual de la foto
            String rutaFoto = usuarioService.obtenerRutaFotoDelEmail(email);

            if (rutaFoto != null && !rutaFoto.isEmpty()) {
                // Eliminar archivo físico
                String nombreArchivo = rutaFoto.replace("/uploads/", "").replace("uploads/", "");
                Path filePath = Paths.get(uploadDir).resolve(nombreArchivo);

                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    System.out.println("✅ Archivo eliminado: " + filePath.toAbsolutePath());
                }
            }

            // Actualizar usuario (establecer foto a null)
            usuarioService.actualizarFotoPerfil(email, null);
            System.out.println("✅ Foto de perfil eliminada para: " + email);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Foto de perfil eliminada exitosamente"
            ));

        } catch (IOException e) {
            System.err.println("❌ Error al eliminar archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Error al eliminar la foto: " + e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("❌ Error general: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Obtener extensión del archivo según el content-type
     */
    private String getExtensionFromContentType(String contentType) {
        switch (contentType) {
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            case "image/webp":
                return ".webp";
            default:
                return ".jpg";
        }
    }
}