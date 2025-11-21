package com.Eduaventuras.controller;

import com.Eduaventuras.dto.RecursoDTO;
import com.Eduaventuras.service.RecursoService;
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

    /**
     * POST /api/recursos/subir
     * Subir un nuevo recurso (docente o admin)
     * Se envía como multipart/form-data
     */
    @PostMapping("/subir")
    public ResponseEntity<?> subirRecurso(
            @RequestParam("file") MultipartFile file,
            @RequestParam("titulo") String titulo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("materiaId") Long materiaId,
            @RequestParam("usuarioId") Long usuarioId) {

        try {
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
     */
    @GetMapping("/{id}/descargar")
    public ResponseEntity<?> descargar(@PathVariable Long id) {
        try {
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
}