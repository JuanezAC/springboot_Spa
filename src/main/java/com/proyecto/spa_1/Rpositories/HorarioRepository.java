package com.proyecto.spa_1.Rpositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.spa_1.Entities.HorarioDisponible;

public interface HorarioRepository extends JpaRepository<HorarioDisponible, Long> {
    List<HorarioDisponible> findByProfesionalId(Long profesionalId);

    long countByProfesionalId(Long profesionalId);

    List<HorarioDisponible> findByProfesionalIdAndFechaAndHora(Long profesionalId, LocalDate fecha, LocalTime hora);

    boolean existsByProfesionalIdAndFechaAndHoraAndIdNot(Long profesionalId, LocalDate fecha, LocalTime hora, Long id);
}
