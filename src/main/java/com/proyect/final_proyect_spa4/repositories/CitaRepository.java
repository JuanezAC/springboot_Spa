package com.proyect.final_proyect_spa4.repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyect.final_proyect_spa4.entities.Cita;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long>{
    List<Cita> findByUsuarioId(Long usuarioId);
    boolean existsByProfesionalIdAndFechaAndHora(Long profesionalId, LocalDate fecha, LocalTime hora);
    Optional<Cita> findByProfesionalIdAndFechaAndHora(Long profesionalId, LocalDate fecha, LocalTime hora);
    List<Cita> findByProfesionalId(Long profesionalId);
}
