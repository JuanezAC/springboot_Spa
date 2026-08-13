package com.proyecto.spa_1.Rpositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.spa_1.Entities.Profesional;

public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {
    boolean existsByCorreo(String correo);

    

}
