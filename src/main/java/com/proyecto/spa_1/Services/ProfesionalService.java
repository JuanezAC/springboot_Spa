package com.proyecto.spa_1.Services;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.proyecto.spa_1.Entities.Profesional;
import com.proyecto.spa_1.Rpositories.CitaRepository;
import com.proyecto.spa_1.Rpositories.HorarioRepository;
import com.proyecto.spa_1.Rpositories.ProSerRepository;
import com.proyecto.spa_1.Rpositories.ProfesionalRepository;

@Service
public class ProfesionalService {
    private final ProfesionalRepository profesionalRepository;
    private final HorarioRepository horarioRepository;
    private final CitaRepository citaRepository;
    private final ProSerRepository proSerRepository;

    public ProfesionalService(ProfesionalRepository profesionalRepository, HorarioRepository horarioRepository, CitaRepository citaRepository, ProSerRepository proSerRepository) {
        this.profesionalRepository = profesionalRepository;
        this.horarioRepository = horarioRepository;
        this.citaRepository = citaRepository;
        this.proSerRepository = proSerRepository;
    }

    public List<Profesional> obtenerTodos() {
        return profesionalRepository.findAll();
    }

    public Profesional obtenerPorId(Long id) {
        return profesionalRepository.findById(id).orElse(null);
    }

    public ResponseEntity<?> guardar(Profesional profesional) {
        if (profesional.getId() != null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "No se debe enviar el ID al registrar un profesional"));
        }

        // Validaciones básicas
        if (profesional.getNombre() == null || profesional.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El nombre del profesional es obligatorio"));
        }

        if (profesional.getEspecialidad() == null || profesional.getEspecialidad().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "La especialidad es obligatoria"));
        }

        if (profesional.getCorreo() == null || profesional.getCorreo().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El correo es obligatorio"));
        }

        String correoLimpio = profesional.getCorreo().trim().toLowerCase();
        if (profesionalRepository.existsByCorreo(correoLimpio)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", "Este correo ya está asignado a otro profesional"));
        }
        profesional.setCorreo(correoLimpio);

        if (profesional.getTelefono() == null || profesional.getTelefono().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El número de teléfono es obligatorio"));
        }

        if (profesional.getActivo() == null) {
            profesional.setActivo(true);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(profesionalRepository.save(profesional));
    }

    public ResponseEntity<?> actualizar(Long id, Profesional profesionalActualizado) {
        Profesional profesionalExistente = obtenerPorId(id);

        if (profesionalExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Profesional no encontrado"));
        }

        // Validación de Nombre
        if (profesionalActualizado.getNombre() != null) {
            if (profesionalActualizado.getNombre().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "El nombre no puede estar vacío"));
            }
            profesionalExistente.setNombre(profesionalActualizado.getNombre().trim());
        }

        // Validación de Especialidad
        if (profesionalActualizado.getEspecialidad() != null) {
            if (profesionalActualizado.getEspecialidad().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "La especialidad no puede estar vacía"));
            }
            profesionalExistente.setEspecialidad(profesionalActualizado.getEspecialidad().trim());
        }

        // Validación de Correo
        if (profesionalActualizado.getCorreo() != null) {
            if (profesionalActualizado.getCorreo().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "El correo no puede estar vacío"));
            }
            
            String correoLimpio = profesionalActualizado.getCorreo().trim().toLowerCase();
            // Si el correo es distinto al que ya tiene, verificamos que no lo use otro
            if (!profesionalExistente.getCorreo().equals(correoLimpio)) {
                if (profesionalRepository.existsByCorreo(correoLimpio)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("mensaje", "El nuevo correo ya está en uso por otro profesional"));
                }
                profesionalExistente.setCorreo(correoLimpio);
            }
        }

        // Validación de Teléfono (Si se envía, no puede estar en blanco)
        if (profesionalActualizado.getTelefono() != null) {
            if (profesionalActualizado.getTelefono().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "El teléfono no puede quedar vacío al actualizar"));
            }
            profesionalExistente.setTelefono(profesionalActualizado.getTelefono().trim());
        }

        // Validación de Estado (Activo/Inactivo)
        if (profesionalActualizado.getActivo() != null) {
            profesionalExistente.setActivo(profesionalActualizado.getActivo());
        }

        return ResponseEntity.ok(profesionalRepository.save(profesionalExistente));
    }

    public ResponseEntity<?> eliminar(Long id) {
        Profesional prof = profesionalRepository.findById(id).orElse(null);
        if (prof == null) return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("mensaje", "Profesional no encontrado"));

        long horarios = horarioRepository.countByProfesionalId(id);
        long citas = citaRepository.countByProfesionalId(id);
        long asignaciones = proSerRepository.countByProfesionalId(id);

        if (horarios > 0 || citas > 0 || asignaciones > 0) {
            String msg = "No se puede eliminar: tiene " + horarios + " horario(s), " +
                        "\n" + citas + " cita(s) y " + "\n" + asignaciones + " servicio(s) asignado(s).";
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", msg));
        }

        profesionalRepository.delete(prof);
        return ResponseEntity.ok(Map.of("mensaje", "Profesional eliminado correctamente"));
    }

}
