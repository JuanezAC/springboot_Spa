package com.proyect.final_proyect_spa4.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyect.final_proyect_spa4.entities.Cita;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long>{
    List<Cita> findByUsuarioId(Long usuarioId);
}
