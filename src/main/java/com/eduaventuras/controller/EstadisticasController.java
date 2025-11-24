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
@RequestMapping("/api/estadisticas")
@CrossOrigin(origins = "*")
public class EstadisticasController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private RecursoService recursoService;

    @Autowired
    private DescargaService descargaService;

    /**
     * GET /api/estadisticas/resumen
     * Obtener resumen de estadísticas públicas (sin autenticación)
     */
    @GetMapping("/resumen")
    public ResponseEntity<?> obtenerResumenPublico() {
        try {
            Map<String, Object> resumen = new HashMap<>();

            resumen.put("totalUsuarios", usuarioService.contarTodosLosUsuarios());
            resumen.put("totalMaterias", materiaService.contarMateriasActivas());
            resumen.put("totalRecursos", recursoService.contarRecursosActivos());
            resumen.put("totalDescargas", descargaService.contarTotalDescargas());

            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error al obtener estadísticas: " + e.getMessage()
            ));
        }
    }
}