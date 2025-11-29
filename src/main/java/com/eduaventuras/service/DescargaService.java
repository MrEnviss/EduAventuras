package com.eduaventuras.service;

import com.eduaventuras.model.Descarga;
import com.eduaventuras.model.Materia;
import com.eduaventuras.model.Recurso;
import com.eduaventuras.model.Usuario;
import com.eduaventuras.repository.DescargaRepository;
import com.eduaventuras.repository.MateriaRepository;
import com.eduaventuras.repository.RecursoRepository;
import com.eduaventuras.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class DescargaService {

    @Autowired
    private DescargaRepository descargaRepository;

    @Autowired
    private RecursoRepository recursoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    /**
     * Registrar una descarga
     */
    public void registrarDescarga(Long recursoId, Long usuarioId) {
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Descarga descarga = new Descarga();
        descarga.setRecurso(recurso);
        descarga.setUsuario(usuario);
        descarga.setFechaDescarga(LocalDateTime.now());

        descargaRepository.save(descarga);
    }

    /**
     * Obtener todas las descargas de un recurso
     */
    public List<Descarga> obtenerDescargasPorRecurso(Long recursoId) {
        return descargaRepository.findByRecursoId(recursoId);
    }

    /**
     * Obtener todas las descargas de un usuario
     */
    public List<Descarga> obtenerDescargasPorUsuario(Long usuarioId) {
        return descargaRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Contar descargas de un recurso
     */
    public long contarDescargasPorRecurso(Long recursoId) {
        return descargaRepository.countByRecursoId(recursoId);
    }

    /**
     * Contar total de descargas del sistema
     */
    public long contarTotalDescargas() {
        return descargaRepository.count();
    }

    /**
     * Obtener recursos más descargados (para dashboard)
     * Retorna un mapa con recursoId, titulo y cantidad de descargas
     */
    public List<Map<String, Object>> obtenerRecursosMasDescargados(int limite) {
        List<Descarga> todasLasDescargas = descargaRepository.findAll();

        // **FILTRO ROBUSTO Y CORREGIDO:** Usa try-catch para capturar el error de Lazy Loading
        Map<Long, Long> conteoPorRecurso = todasLasDescargas.stream()
                .filter(d -> {
                    try {
                        // Acceder al ID del recurso forzará la inicialización. Si falla, descartamos.
                        return d.getRecurso() != null && d.getRecurso().getId() != null;
                    } catch (Exception e) {
                        // Captura la EntityNotFoundException (la causa del error 400)
                        return false;
                    }
                })
                .collect(Collectors.groupingBy(
                        d -> d.getRecurso().getId(),
                        Collectors.counting()
                ));

        // Ordenar por cantidad de descargas y tomar los primeros N
        return conteoPorRecurso.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limite)
                .map(entry -> {
                    Recurso recurso = recursoRepository.findById(entry.getKey())
                            .orElse(null);

                    Map<String, Object> info = new HashMap<>();
                    if (recurso != null) {
                        info.put("recursoId", recurso.getId());
                        info.put("titulo", recurso.getTitulo());
                        info.put("materia", recurso.getMateria() != null ? recurso.getMateria().getNombre() : "N/A");
                        info.put("cantidadDescargas", entry.getValue());
                    }
                    return info;
                })
                .filter(m -> !m.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Obtener descargas recientes (para dashboard)
     */
    public List<Map<String, Object>> obtenerDescargasRecientes(int limite) {
        return descargaRepository.findAll().stream()
                .filter(d -> {
                    try {
                        // **FILTRO ROBUSTO Y CORREGIDO:** Chequeo completo para Recurso y Usuario
                        return d.getRecurso() != null && d.getUsuario() != null &&
                                d.getRecurso().getId() != null && d.getUsuario().getId() != null;
                    } catch (Exception e) {
                        // Capturar excepción y descartar
                        return false;
                    }
                })
                .sorted((d1, d2) -> d2.getFechaDescarga().compareTo(d1.getFechaDescarga()))
                .limit(limite)
                .map(descarga -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("recursoTitulo", descarga.getRecurso().getTitulo());
                    info.put("usuarioNombre", descarga.getUsuario().getNombre() + " " +
                            descarga.getUsuario().getApellido());
                    info.put("fecha", descarga.getFechaDescarga());
                    return info;
                })
                .collect(Collectors.toList());
    }

    /**
     * NUEVA FUNCIÓN: Obtener el conteo de recursos por materia.
     */
    public List<Map<String, Object>> obtenerConteoRecursosPorMateria() {
        // Obtenemos solo las materias activas
        List<Materia> materias = materiaRepository.findByActivo(true); // Requiere MateriaRepository.java

        return materias.stream()
                .map(materia -> {
                    // Contamos los recursos activos por el ID de la materia
                    long conteo = recursoRepository.countByMateriaId(materia.getId()); // Requiere RecursoRepository.java

                    Map<String, Object> info = new HashMap<>();
                    info.put("nombreMateria", materia.getNombre());
                    info.put("conteoRecursos", conteo);
                    return info;
                })
                .collect(Collectors.toList());
    }
}