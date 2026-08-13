package com.proyecto.spa_1.Rpositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.spa_1.Entities.ProfesionalServicio;

public interface ProSerRepository extends JpaRepository<ProfesionalServicio, Long> {
    List<ProfesionalServicio> findByProfesionalId(Long profesionalId);

    List<ProfesionalServicio> findByServicioId(Long servicioId);

    boolean existsByProfesionalIdAndServicioId(Long profesionalId, Long servicioId);

    long countByProfesionalId(Long profesionalId);

    long countByServicioId(Long servicioId);
}
