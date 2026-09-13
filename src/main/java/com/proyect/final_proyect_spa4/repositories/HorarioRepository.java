package com.proyect.final_proyect_spa4.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.proyect.final_proyect_spa4.entities.HorarioDisponible;
@Repository
public interface HorarioRepository extends JpaRepository<HorarioDisponible, Long>{
    List<HorarioDisponible> findByProfesionalId(Long profesionalId);

    @Query("SELECT h FROM HorarioDisponible h WHERE h.disponible = true " +
           "AND h.profesional.estado = true " +
           "AND NOT EXISTS (SELECT c FROM Cita c WHERE c.profesional = h.profesional AND c.fecha = h.fecha AND c.hora = h.hora)")
    List<HorarioDisponible> findDisponibles();
}
