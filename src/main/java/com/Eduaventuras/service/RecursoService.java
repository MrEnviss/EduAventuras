package com.Eduaventuras.service;

import com.Eduaventuras.dto.RecursoDTO;
import com.Eduaventuras.model.Materia;
import com.Eduaventuras.model.Recurso;
import com.Eduaventuras.model.Usuario;
import com.Eduaventuras.repository.MateriaRepository;
import com.Eduaventuras.repository.RecursoRepository;
import com.Eduaventuras.repository.UsuarioRepository;
import com.Eduaventuras.repository.DescargaRepository;
import com.Eduaventuras.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecursoService {

    @Autowired
    private RecursoRepository recursoRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DescargaRepository descargaRepository;

    /**
     * Subir un nuevo recurso (docente o admin)
     */
    public RecursoDTO subirRecurso(MultipartFile file, String titulo, String descripcion,
                                   Long materiaId, Long usuarioId) throws IOException {

        // Validar que la materia exista
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        // Validar que el usuario exista
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Guardar archivo físico
        String rutaArchivo = FileUtil.guardarArchivo(file, materia.getNombre());

        // Crear registro en BD
        Recurso recurso = new Recurso();
        recurso.setTitulo(titulo);
        recurso.setDescripcion(descripcion);
        recurso.setNombreArchivo(file.getOriginalFilename());
        recurso.setRutaArchivo(rutaArchivo);
        recurso.setMateria(materia);
        recurso.setSubidoPor(usuario);
        recurso.setTamanioBytes(file.getSize());
        recurso.setActivo(true);

        Recurso guardado = recursoRepository.save(recurso);
        return convertirADTO(guardado);
    }

    /**
     * Listar todos los recursos activos
     */
    public List<RecursoDTO> listarActivos() {
        return recursoRepository.findByActivo(true).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar TODOS los recursos (activos e inactivos) - Solo admin
     */
    public List<RecursoDTO> listarTodos() {
        return recursoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar recursos por materia
     */
    public List<RecursoDTO> listarPorMateria(Long materiaId) {
        return recursoRepository.findByMateriaId(materiaId).stream()
                .filter(Recurso::getActivo)
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar recurso por ID
     */
    public RecursoDTO buscarPorId(Long id) {
        Recurso recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
        return convertirADTO(recurso);
    }

    /**
     * Obtener archivo para descarga
     */
    public byte[] descargarRecurso(Long id) throws IOException {
        Recurso recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        return FileUtil.leerArchivo(recurso.getRutaArchivo());
    }

    /**
     * Obtener nombre del archivo original
     */
    public String obtenerNombreArchivo(Long id) {
        Recurso recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
        return recurso.getNombreArchivo();
    }

    /**
     * Eliminar recurso (admin o quien lo subió)
     */
    public void eliminar(Long id) throws IOException {
        Recurso recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        // Eliminar archivo físico
        FileUtil.eliminarArchivo(recurso.getRutaArchivo());

        // Marcar como inactivo en BD
        recurso.setActivo(false);
        recursoRepository.save(recurso);
    }

    /**
     * Reactivar recurso eliminado (solo admin)
     */
    public RecursoDTO reactivar(Long id) {
        Recurso recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
        recurso.setActivo(true);
        Recurso reactivado = recursoRepository.save(recurso);
        return convertirADTO(reactivado);
    }

    /**
     * Contar recursos activos (para dashboard)
     */
    public long contarRecursosActivos() {
        return recursoRepository.findByActivo(true).size();
    }

    /**
     * Obtener recursos recientes (para dashboard)
     */
    public List<RecursoDTO> obtenerRecursosRecientes(int limite) {
        return recursoRepository.findAll().stream()
                .filter(Recurso::getActivo)
                .sorted((r1, r2) -> r2.getFechaSubida().compareTo(r1.getFechaSubida()))
                .limit(limite)
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertir entidad a DTO
     */
    private RecursoDTO convertirADTO(Recurso recurso) {
        RecursoDTO dto = new RecursoDTO();
        dto.setId(recurso.getId());
        dto.setTitulo(recurso.getTitulo());
        dto.setDescripcion(recurso.getDescripcion());
        dto.setNombreArchivo(recurso.getNombreArchivo());
        dto.setMateriaId(recurso.getMateria().getId());
        dto.setMateriaNombre(recurso.getMateria().getNombre());
        dto.setSubidoPorId(recurso.getSubidoPor().getId());
        dto.setSubidoPorNombre(recurso.getSubidoPor().getNombre() + " " +
                recurso.getSubidoPor().getApellido());
        dto.setFechaSubida(recurso.getFechaSubida());
        dto.setTamanioBytes(recurso.getTamanioBytes());

        // Contar descargas
        long descargas = descargaRepository.countByRecursoId(recurso.getId());
        dto.setCantidadDescargas(descargas);

        return dto;
    }
}