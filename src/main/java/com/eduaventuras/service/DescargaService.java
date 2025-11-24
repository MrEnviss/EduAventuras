package com.eduaventuras.service;

import com.eduaventuras.model.Descarga;
import com.eduaventuras.model.Recurso;
import com.eduaventuras.model.Usuario;
import com.eduaventuras.repository.DescargaRepository;
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

    /**
     * Registrar una descarga
     * Se llama automáticamente cuando alguien descarga un recurso
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

        // Agrupar descargas por recurso y contar
        Map<Long, Long> conteoPorRecurso = todasLasDescargas.stream()
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
                        info.put("materia", recurso.getMateria().getNombre());
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
}