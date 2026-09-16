package com.proyect.final_proyect_spa4.repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyect.final_proyect_spa4.entities.HorarioDisponible;
@Repository
public interface HorarioRepository extends JpaRepository<HorarioDisponible, Long>{
    List<HorarioDisponible> findByProfesionalId(Long profesionalId);

    @Query("SELECT h FROM HorarioDisponible h WHERE h.disponible = true " +
           "AND h.profesional.estado = true " +
           "AND NOT EXISTS (SELECT c FROM Cita c WHERE c.profesional = h.profesional AND c.fecha = h.fecha AND c.hora = h.hora)")
    List<HorarioDisponible> findDisponibles();

    @Query("SELECT h FROM HorarioDisponible h WHERE h.disponible = true " +
           "AND (h.fecha < :hoy OR (h.fecha = :hoy AND h.hora < :ahora))")
    List<HorarioDisponible> findExpirados(@Param("hoy") LocalDate hoy, @Param("ahora") LocalTime ahora);

    @Modifying
    @Query("UPDATE HorarioDisponible h SET h.disponible = false " +
           "WHERE h.disponible = true " +
           "AND (h.fecha < :hoy OR (h.fecha = :hoy AND h.hora < :ahora))")
    int marcarExpirados(@Param("hoy") LocalDate hoy, @Param("ahora") LocalTime ahora);
}
