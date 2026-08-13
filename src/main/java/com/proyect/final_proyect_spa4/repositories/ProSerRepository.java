package com.proyect.final_proyect_spa4.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyect.final_proyect_spa4.entities.ProfesionalServicio;

@Repository
public interface ProSerRepository extends JpaRepository<ProfesionalServicio, Long>{
    List<ProfesionalServicio> findByProfesionalId(Long profesionalId);
    List<ProfesionalServicio> findByServicioId(Long servicioId);
}
