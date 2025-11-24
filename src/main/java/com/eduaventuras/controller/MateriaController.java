package com.eduaventuras.controller;

import com.eduaventuras.dto.MateriaDTO;
import com.eduaventuras.service.MateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/materias")
@CrossOrigin(origins = "*")
public class MateriaController {

    @Autowired
    private MateriaService materiaService;

    /**
     * POST /api/materias
     * Crear una nueva materia (solo admin)
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody MateriaDTO materiaDTO) {
        try {
            MateriaDTO materia = materiaService.crear(materiaDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Materia creada exitosamente", "materia", materia));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/materias
     * Listar todas las materias activas
     */
    @GetMapping
    public ResponseEntity<List<MateriaDTO>> listarActivas() {
        List<MateriaDTO> materias = materiaService.listarActivas();
        return ResponseEntity.ok(materias);
    }

    /**
     * GET /api/materias/todas
     * Listar todas las materias (activas e inactivas) - solo admin
     */
    @GetMapping("/todas")
    public ResponseEntity<List<MateriaDTO>> listarTodas() {
        List<MateriaDTO> materias = materiaService.listarTodas();
        return ResponseEntity.ok(materias);
    }

    /**
     * GET /api/materias/{id}
     * Buscar materia por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            MateriaDTO materia = materiaService.buscarPorId(id);
            return ResponseEntity.ok(materia);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PUT /api/materias/{id}
     * Actualizar una materia (solo admin)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody MateriaDTO materiaDTO) {
        try {
            MateriaDTO materia = materiaService.actualizar(id, materiaDTO);
            return ResponseEntity.ok(Map.of("mensaje", "Materia actualizada exitosamente", "materia", materia));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/materias/{id}
     * Eliminar una materia (solo admin)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            materiaService.eliminar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Materia eliminada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}