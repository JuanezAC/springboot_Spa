package com.proyecto.spa_1.Services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.proyecto.spa_1.Entities.HorarioDisponible;
import com.proyecto.spa_1.Entities.Profesional;
import com.proyecto.spa_1.Rpositories.CitaRepository;
import com.proyecto.spa_1.Rpositories.HorarioRepository;
import com.proyecto.spa_1.Rpositories.ProfesionalRepository;

@Service
public class HorarioService {
    private final HorarioRepository horarioRepository;
    private final ProfesionalRepository profesionalRepository;
    private final CitaRepository citaRepository;

    public HorarioService(HorarioRepository horarioRepository, ProfesionalRepository profesionalRepository, CitaRepository citaRepository) {
        this.horarioRepository = horarioRepository;
        this.profesionalRepository = profesionalRepository;
        this.citaRepository = citaRepository;
    }

    public List<HorarioDisponible> obtenerTodos() {
        return horarioRepository.findAll();
    }

    public List<HorarioDisponible> obtenerPorProfesional(Long profesionalId) {
        return horarioRepository.findByProfesionalId(profesionalId);
    }

    public HorarioDisponible obtenerPorId(Long id) {
        return horarioRepository.findById(id).orElse(null);
    }

    public ResponseEntity<?> guardar(HorarioDisponible horario) {
        // Validar que no envíen ID al crear

        if (horario.getId() != null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "No se debe enviar el ID al crear un nuevo horario"));
        }

        // Validar Profesional
        if (horario.getProfesional() == null || horario.getProfesional().getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Debe asignar un profesional al horario"));
        }

        Profesional profesional = profesionalRepository.findById(horario.getProfesional().getId()).orElse(null);
        if (profesional == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "El profesional no existe"));
        }

        // REGLA: No asignar a profesionales inactivos
        if (!profesional.getActivo()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "No se pueden crear horarios para un profesional inactivo"));
        }

        // Validar Fecha y hora ( No nulos)
        if (horario.getFecha() == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "La fecha es obligatoria"));
        }

        if (horario.getHora() == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "La hora es obligatoria"));
        }

        //Logica validacion de tiempo
        LocalDate hoy = LocalDate.now();
        if (horario.getFecha().isBefore(hoy)) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "La fecha no puede ser anterior a hoy"));
        }

        if (horario.getFecha().equals(hoy)) {
            if (horario.getHora().isBefore(LocalTime.now())) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "La hora seleccionada ya ha pasado"));
            }
        }

        if (horario.getDisponible() == null) {
            horario.setDisponible(true);
        }

        List<HorarioDisponible> existentes = horarioRepository
            .findByProfesionalIdAndFechaAndHora(
                profesional.getId(),
                horario.getFecha(),
                horario.getHora()
            );

        if (!existentes.isEmpty()) {
            return ResponseEntity.badRequest().body(
                Map.of("mensaje", "Ya existe un horario registrado para este profesional en esa fecha y hora")
            );
        }

        horario.setProfesional(profesional);
        return ResponseEntity.status(HttpStatus.CREATED).body(horarioRepository.save(horario));
    }

    public ResponseEntity<?> actualizar(Long id, HorarioDisponible horarioActualizado) {


        HorarioDisponible horarioExistente = obtenerPorId(id);

        

        if (horarioExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Horario no encontrado"));
        }

        boolean existe = horarioRepository.existsByProfesionalIdAndFechaAndHoraAndIdNot(
            horarioActualizado.getProfesional().getId(),
            horarioActualizado.getFecha(),
            horarioActualizado.getHora(),
            horarioExistente.getId()
        );

        if (existe) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Ya existe un horario registrado para este profesional en esa fecha y hora"));
        }

        //Validaciones de actualizacion
        if (horarioActualizado.getFecha() != null) {
            if (horarioActualizado.getFecha().isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "La nueva fecha no puede ser pasada"));
            }
            horarioExistente.setFecha(horarioActualizado.getFecha());
        }

        if (horarioActualizado.getHora() != null) {
            // Si la fecha es hoy o se cambió a hoy, validar hora
            LocalDate fechaValidar;

            if (horarioActualizado.getFecha() != null) {
                // Si el admin envió una nueva fecha, usamos esa para validar
                fechaValidar = horarioActualizado.getFecha();
            } else {
                // Si no envió fecha (es null), usamos la que ya estaba guardada en la base de datos
                fechaValidar = horarioExistente.getFecha();
            }

            if (fechaValidar.equals(LocalDate.now()) && horarioActualizado.getHora().isBefore(LocalTime.now())) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "La nueva hora seleccionada ya ha pasado"));
            }
            horarioExistente.setHora(horarioActualizado.getHora());
        }

        return ResponseEntity.ok(horarioRepository.save(horarioExistente));


    }

    public ResponseEntity<?> eliminar(Long id) {
        HorarioDisponible horario = horarioRepository.findById(id).orElse(null);
        if (horario == null) return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("mensaje", "Horario no encontrado"));

        long citas = citaRepository.countByHorarioId(id);
        if (citas > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensaje", "No se puede eliminar: tiene " + citas + " cita(s) agendada(s)."));
        }

        horarioRepository.delete(horario);
        return ResponseEntity.ok(Map.of("mensaje", "Horario eliminado correctamente"));
    }
}
