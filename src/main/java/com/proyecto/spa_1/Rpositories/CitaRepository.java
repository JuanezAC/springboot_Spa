package com.proyecto.spa_1.Rpositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.spa_1.Entities.Cita;

public interface CitaRepository extends JpaRepository<Cita, Long>{
    List<Cita> findByUsuarioId(Long usuarioId);

    long countByUsuarioId(Long usuarioId);

    long countByProfesionalId(Long profesionalId);

    long countByServicioId(Long servicioId);

    long countByHorarioId(Long horarioId);
}
