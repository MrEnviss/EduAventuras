package com.eduaventuras.controller;

import com.eduaventuras.service.UsuarioService;
import com.eduaventuras.service.MateriaService;
import com.eduaventuras.service.RecursoService;
import com.eduaventuras.service.DescargaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "*")
public class AdminDashboardController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private RecursoService recursoService;

    @Autowired
    private DescargaService descargaService;

    /**
     * GET /api/admin/dashboard/estadisticas
     * Obtener estadísticas generales del sistema (solo admin)
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // Estadísticas de usuarios
            Map<String, Long> usuarios = new HashMap<>();
            usuarios.put("totalEstudiantes", usuarioService.contarPorRol(com.eduaventuras.model.Rol.ESTUDIANTE));
            usuarios.put("totalDocentes", usuarioService.contarPorRol(com.eduaventuras.model.Rol.DOCENTE));
            usuarios.put("totalAdmins", usuarioService.contarPorRol(com.eduaventuras.model.Rol.ADMIN));
            usuarios.put("totalUsuarios", usuarioService.contarTodosLosUsuarios());
            stats.put("usuarios", usuarios);

            // Estadísticas de contenido
            Map<String, Long> contenido = new HashMap<>();
            contenido.put("totalMaterias", materiaService.contarMateriasActivas());
            contenido.put("totalRecursos", recursoService.contarRecursosActivos());
            contenido.put("totalDescargas", descargaService.contarTotalDescargas());
            stats.put("contenido", contenido);

            // Recursos más descargados (top 5)
            stats.put("recursosPopulares", descargaService.obtenerRecursosMasDescargados(5));

            // Actividad reciente
            stats.put("usuariosRecientes", usuarioService.obtenerUsuariosRecientes(5));
            stats.put("recursosRecientes", recursoService.obtenerRecursosRecientes(5));

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error al obtener estadísticas: " + e.getMessage()
            ));
        }
    }

    /**
     * GET /api/admin/dashboard/resumen
     * Obtener resumen ejecutivo básico
     */
    @GetMapping("/resumen")
    public ResponseEntity<?> obtenerResumen() {
        try {
            Map<String, Object> resumen = new HashMap<>();

            resumen.put("totalUsuarios", usuarioService.contarTodosLosUsuarios());
            resumen.put("totalMaterias", materiaService.contarMateriasActivas());
            resumen.put("totalRecursos", recursoService.contarRecursosActivos());
            resumen.put("totalDescargas", descargaService.contarTotalDescargas());

            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error al obtener resumen: " + e.getMessage()
            ));
        }
    }
}