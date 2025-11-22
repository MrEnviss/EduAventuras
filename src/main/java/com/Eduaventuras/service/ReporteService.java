package com.Eduaventuras.service;

import com.Eduaventuras.dto.EstadisticasDTO;
import com.Eduaventuras.dto.MateriaDTO;
import com.Eduaventuras.dto.RecursoDTO;
import com.Eduaventuras.model.Rol;
import com.Eduaventuras.util.PdfGenerator;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private RecursoService recursoService;

    @Autowired
    private DescargaService descargaService;

    /**
     * Generar reporte PDF completo con estadísticas del sistema
     */
    public byte[] generarReporteEstadisticas() {
        try {
            // Crear documento
            Document document = PdfGenerator.crearDocumento();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);

            document.open();

            // Encabezado
            PdfGenerator.agregarEncabezado(document, "EDUAVENTURAS - REPORTE DE ESTADÍSTICAS");

            // ===== SECCIÓN 1: ESTADÍSTICAS DE USUARIOS =====
            PdfGenerator.agregarSeccion(document, "📊 ESTADÍSTICAS DE USUARIOS");

            long totalUsuarios = usuarioService.listarTodos().size();
            long totalEstudiantes = usuarioService.contarPorRol(Rol.ESTUDIANTE);
            long totalDocentes = usuarioService.contarPorRol(Rol.DOCENTE);
            long totalAdmins = usuarioService.contarPorRol(Rol.ADMIN);

            Map<String, String> datosUsuarios = new LinkedHashMap<>();
            datosUsuarios.put("Total de usuarios registrados:", String.valueOf(totalUsuarios));
            datosUsuarios.put("Estudiantes:", String.valueOf(totalEstudiantes));
            datosUsuarios.put("Docentes:", String.valueOf(totalDocentes));
            datosUsuarios.put("Administradores:", String.valueOf(totalAdmins));

            PdfGenerator.agregarTablaDatos(document, datosUsuarios);
            PdfGenerator.agregarSeparador(document);

            // ===== SECCIÓN 2: ESTADÍSTICAS DE RECURSOS =====
            PdfGenerator.agregarSeccion(document, "📚 ESTADÍSTICAS DE RECURSOS");

            List<RecursoDTO> recursosActivos = recursoService.listarActivos();
            long totalRecursos = recursosActivos.size();

            Map<String, String> datosRecursos = new LinkedHashMap<>();
            datosRecursos.put("Total de recursos disponibles:", String.valueOf(totalRecursos));

            PdfGenerator.agregarTablaDatos(document, datosRecursos);

            // Recursos por materia
            PdfGenerator.agregarParrafo(document, "\nDistribución por materia:");

            List<MateriaDTO> materias = materiaService.listarActivas();
            Map<String, String> recursosPorMateria = new LinkedHashMap<>();

            for (MateriaDTO materia : materias) {
                long cantidad = recursoService.listarPorMateria(materia.getId()).size();
                recursosPorMateria.put("  • " + materia.getNombre() + ":", String.valueOf(cantidad) + " recursos");
            }

            PdfGenerator.agregarTablaDatos(document, recursosPorMateria);
            PdfGenerator.agregarSeparador(document);

            // ===== SECCIÓN 3: ESTADÍSTICAS DE DESCARGAS =====
            PdfGenerator.agregarSeccion(document, "📥 ESTADÍSTICAS DE DESCARGAS");

            long totalDescargas = descargaService.contarTotalDescargas();

            Map<String, String> datosDescargas = new LinkedHashMap<>();
            datosDescargas.put("Total de descargas realizadas:", String.valueOf(totalDescargas));

            PdfGenerator.agregarTablaDatos(document, datosDescargas);
            PdfGenerator.agregarSeparador(document);

            // ===== SECCIÓN 4: TOP RECURSOS MÁS DESCARGADOS =====
            PdfGenerator.agregarSeccion(document, "🏆 TOP 10 RECURSOS MÁS DESCARGADOS");

            List<RecursoDTO> topRecursos = recursosActivos.stream()
                    .sorted((r1, r2) -> Long.compare(r2.getCantidadDescargas(), r1.getCantidadDescargas()))
                    .limit(10)
                    .collect(Collectors.toList());

            if (!topRecursos.isEmpty()) {
                String[] headers = {"#", "Título", "Materia", "Descargas"};
                List<String[]> rows = new ArrayList<>();

                int posicion = 1;
                for (RecursoDTO recurso : topRecursos) {
                    rows.add(new String[]{
                            String.valueOf(posicion++),
                            recurso.getTitulo(),
                            recurso.getMateriaNombre(),
                            String.valueOf(recurso.getCantidadDescargas())
                    });
                }

                PdfGenerator.agregarTabla(document, headers, rows);
            } else {
                PdfGenerator.agregarParrafo(document, "No hay recursos con descargas registradas aún.");
            }

            // ===== PIE DE PÁGINA =====
            PdfGenerator.agregarSeparador(document);
            PdfGenerator.agregarParrafo(document,
                    "\nEste reporte fue generado automáticamente por el sistema EduAventuras.");
            PdfGenerator.agregarParrafo(document,
                    "Plataforma educativa gratuita para niños de escasos recursos.");

            document.close();

            return baos.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar el reporte PDF: " + e.getMessage());
        }
    }

    /**
     * Generar reporte de una materia específica
     */
    public byte[] generarReporteMateria(Long materiaId) {
        try {
            MateriaDTO materia = materiaService.buscarPorId(materiaId);
            List<RecursoDTO> recursos = recursoService.listarPorMateria(materiaId);

            Document document = PdfGenerator.crearDocumento();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);

            document.open();

            // Encabezado
            PdfGenerator.agregarEncabezado(document,
                    "REPORTE DE MATERIA: " + materia.getNombre().toUpperCase());

            // Información general
            Map<String, String> datosMateria = new LinkedHashMap<>();
            datosMateria.put("Nombre:", materia.getNombre());
            datosMateria.put("Descripción:", materia.getDescripcion());
            datosMateria.put("Total de recursos:", String.valueOf(recursos.size()));

            long totalDescargas = recursos.stream()
                    .mapToLong(RecursoDTO::getCantidadDescargas)
                    .sum();
            datosMateria.put("Total de descargas:", String.valueOf(totalDescargas));

            PdfGenerator.agregarTablaDatos(document, datosMateria);
            PdfGenerator.agregarSeparador(document);

            // Lista de recursos
            PdfGenerator.agregarSeccion(document, "📚 RECURSOS DISPONIBLES");

            if (!recursos.isEmpty()) {
                String[] headers = {"Título", "Subido por", "Descargas"};
                List<String[]> rows = new ArrayList<>();

                for (RecursoDTO recurso : recursos) {
                    rows.add(new String[]{
                            recurso.getTitulo(),
                            recurso.getSubidoPorNombre(),
                            String.valueOf(recurso.getCantidadDescargas())
                    });
                }

                PdfGenerator.agregarTabla(document, headers, rows);
            } else {
                PdfGenerator.agregarParrafo(document, "No hay recursos disponibles en esta materia.");
            }

            document.close();

            return baos.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar el reporte: " + e.getMessage());
        }
    }
}