package com.Eduaventuras.repository;

import com.Eduaventuras.model.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {
    List<Recurso> findByMateriaId(Long materiaId);
    List<Recurso> findByActivo(Boolean activo);
    long countByMateriaId(Long materiaId);
}