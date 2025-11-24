package com.eduaventuras.controller;

import com.eduaventuras.dto.RecursoDTO;
import com.eduaventuras.service.RecursoService;
import com.eduaventuras.service.DescargaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recursos")
@CrossOrigin(origins = "*")
public class RecursoController {

    @Autowired
    private RecursoService recursoService;

    @Autowired
    private DescargaService descargaService;

    /**
     * POST /api/recursos/subir
     * Subir un nuevo recurso (docente o admin)
     * El usuario se obtiene automáticamente del token JWT
     */
    @PostMapping(value = "/subir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirRecurso(
            @RequestParam("file") MultipartFile file,
            @RequestParam("titulo") String titulo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("materiaId") Long materiaId,
            @RequestParam("usuarioId") Long usuarioId) {  // ← VOLVER A RECIBIR POR PARÁMETRO

        try {
            // YA NO USAR AuthUtil, usar el parámetro directo
            RecursoDTO recurso = recursoService.subirRecurso(file, titulo, descripcion,
                    materiaId, usuarioId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Recurso subido exitosamente", "recurso", recurso));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/recursos
     * Listar todos los recursos activos
     */
    @GetMapping
    public ResponseEntity<List<RecursoDTO>> listarActivos() {
        List<RecursoDTO> recursos = recursoService.listarActivos();
        return ResponseEntity.ok(recursos);
    }

    /**
     * GET /api/recursos/todos
     * Listar TODOS los recursos (activos e inactivos) - Solo admin
     */
    @GetMapping("/todos")
    public ResponseEntity<List<RecursoDTO>> listarTodos() {
        List<RecursoDTO> recursos = recursoService.listarTodos();
        return ResponseEntity.ok(recursos);
    }

    /**
     * GET /api/recursos/materia/{materiaId}
     * Listar recursos por materia
     */
    @GetMapping("/materia/{materiaId}")
    public ResponseEntity<List<RecursoDTO>> listarPorMateria(@PathVariable Long materiaId) {
        List<RecursoDTO> recursos = recursoService.listarPorMateria(materiaId);
        return ResponseEntity.ok(recursos);
    }

    /**
     * GET /api/recursos/{id}
     * Buscar recurso por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            RecursoDTO recurso = recursoService.buscarPorId(id);
            return ResponseEntity.ok(recurso);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/recursos/{id}/descargar
     * Descargar un recurso (archivo PDF)
     * Registra automáticamente la descarga para estadísticas si se proporciona usuarioId
     */
    @GetMapping("/{id}/descargar")
    public ResponseEntity<?> descargar(
            @PathVariable Long id,
            @RequestParam(required = false) Long usuarioId) {
        try {
            // Registrar la descarga si se proporcionó usuarioId
            if (usuarioId != null) {
                descargaService.registrarDescarga(id, usuarioId);
            }

            byte[] archivo = recursoService.descargarRecurso(id);
            String nombreArchivo = recursoService.obtenerNombreArchivo(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", nombreArchivo);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(archivo);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/recursos/{id}
     * Eliminar un recurso (admin o quien lo subió)
     * NOTA: Esto es "soft delete", solo marca como inactivo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            recursoService.eliminar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Recurso eliminado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/recursos/{id}/restaurar
     * Restaurar un recurso eliminado (solo admin)
     */
    @PutMapping("/{id}/restaurar")
    public ResponseEntity<?> restaurar(@PathVariable Long id) {
        try {
            RecursoDTO recurso = recursoService.reactivar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Recurso restaurado exitosamente", "recurso", recurso));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}