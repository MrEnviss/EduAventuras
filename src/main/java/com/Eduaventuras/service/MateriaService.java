package com.Eduaventuras.service;

import com.Eduaventuras.dto.MateriaDTO;
import com.Eduaventuras.model.Materia;
import com.Eduaventuras.repository.MateriaRepository;
import com.Eduaventuras.repository.RecursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MateriaService {

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private RecursoRepository recursoRepository;

    /**
     * Crear una nueva materia (solo admin)
     */
    public MateriaDTO crear(MateriaDTO materiaDTO) {
        Materia materia = new Materia();
        materia.setNombre(materiaDTO.getNombre());
        materia.setDescripcion(materiaDTO.getDescripcion());
        materia.setIcono(materiaDTO.getIcono());
        materia.setActivo(true);

        Materia guardada = materiaRepository.save(materia);
        return convertirADTO(guardada);
    }

    /**
     * Listar todas las materias activas
     */
    public List<MateriaDTO> listarActivas() {
        return materiaRepository.findByActivo(true).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar todas las materias (activas e inactivas)
     */
    public List<MateriaDTO> listarTodas() {
        return materiaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar materia por ID
     */
    public MateriaDTO buscarPorId(Long id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
        return convertirADTO(materia);
    }

    /**
     * Actualizar una materia (solo admin)
     */
    public MateriaDTO actualizar(Long id, MateriaDTO materiaDTO) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        materia.setNombre(materiaDTO.getNombre());
        materia.setDescripcion(materiaDTO.getDescripcion());
        materia.setIcono(materiaDTO.getIcono());

        Materia actualizada = materiaRepository.save(materia);
        return convertirADTO(actualizada);
    }

    /**
     * Eliminar materia (solo admin)
     */
    public void eliminar(Long id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        // Verificar que no tenga recursos asociados
        long cantidadRecursos = recursoRepository.countByMateriaId(id);
        if (cantidadRecursos > 0) {
            throw new RuntimeException("No se puede eliminar una materia con recursos asociados");
        }

        materia.setActivo(false);
        materiaRepository.save(materia);
    }

    /**
     * Contar materias activas (para dashboard)
     */
    public long contarMateriasActivas() {
        return materiaRepository.findByActivo(true).size();
    }

    /**
     * Convertir entidad a DTO
     */
    private MateriaDTO convertirADTO(Materia materia) {
        MateriaDTO dto = new MateriaDTO();
        dto.setId(materia.getId());
        dto.setNombre(materia.getNombre());
        dto.setDescripcion(materia.getDescripcion());
        dto.setIcono(materia.getIcono());

        // Contar recursos asociados
        long cantidadRecursos = recursoRepository.countByMateriaId(materia.getId());
        dto.setCantidadRecursos(cantidadRecursos);

        return dto;
    }
}