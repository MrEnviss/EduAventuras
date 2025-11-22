package com.Eduaventuras.service;

import com.Eduaventuras.model.Descarga;
import com.Eduaventuras.model.Recurso;
import com.Eduaventuras.model.Usuario;
import com.Eduaventuras.repository.DescargaRepository;
import com.Eduaventuras.repository.RecursoRepository;
import com.Eduaventuras.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
}