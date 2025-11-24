package com.eduaventuras.repository;

import com.eduaventuras.model.Descarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DescargaRepository extends JpaRepository<Descarga, Long> {
    List<Descarga> findByRecursoId(Long recursoId);
    List<Descarga> findByUsuarioId(Long usuarioId);
    long countByRecursoId(Long recursoId);
}