package com.proyect.final_proyect_spa4.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proyect.final_proyect_spa4.entities.Profesional;
import com.proyect.final_proyect_spa4.entities.ProfesionalServicio;
import com.proyect.final_proyect_spa4.repositories.ProSerRepository;
import com.proyect.final_proyect_spa4.repositories.ProfesionalRepository;

@Service
public class ProfesionalService {

    private final ProfesionalRepository profesionalRepository;
    private final ProSerRepository proSerRepository;

    public ProfesionalService(ProfesionalRepository profesionalRepository, ProSerRepository proSerRepository) {
        this.profesionalRepository = profesionalRepository;
        this.proSerRepository = proSerRepository;
    }

    public List<Profesional> buscarTodosProfesionales() {
        return profesionalRepository.findAll();
    }

    public Profesional buscarProfesionalPorId(Long id) {
        return profesionalRepository.findById(id).orElse(null);
    }

    public List<Profesional> buscarProfesionalesPorServicio(Long servicioId) {
        List<ProfesionalServicio> proSers = proSerRepository.findByServicioId(servicioId);
        
        List<Profesional> profesionales = new ArrayList<>();
        for (ProfesionalServicio proSer : proSers) {
            profesionales.add(proSer.getProfesional());
        }
        return profesionales;
    }

    public ResponseEntity<?> guardarProfesional(Profesional profesional) {
        if (profesional.getId() != null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "No se debe enviar el ID al registrar un profesional"));
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
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", "Este correo ya está asignado a otro profesional"));
        }
        profesional.setCorreo(correoLimpio);

        if (profesional.getTelefono() == null || profesional.getTelefono().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El número de teléfono es obligatorio"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(profesionalRepository.save(profesional));
    }

    public ResponseEntity<?> actualizarProfesional(Long id, Profesional profesionalActualizado) {
        Profesional profesionalExistente = buscarProfesionalPorId(id);

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
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", "El teléfono no puede quedar vacío al actualizar"));
            }
            profesionalExistente.setTelefono(profesionalActualizado.getTelefono().trim());
        }

        // Validación de Estado (Activo/Inactivo)
        profesionalExistente.setEstado(profesionalActualizado.getEstado());

        return ResponseEntity.ok(profesionalRepository.save(profesionalExistente));
    }

    public ResponseEntity<?> eliminarProfesional(Long id) {
        // 1. Buscamos si existe
        Profesional profesionalExistente = buscarProfesionalPorId(id);

        if (profesionalExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Profesional no encontrado para desactivar"));
        }

        // 2. Verificamos si ya estaba desactivado (opcional, para ser más precisos)
        if (!profesionalExistente.getEstado()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "El profesional ya se encuentra inactivo"));
        }

        // 3. Borrado Lógico: Cambiamos el estado en lugar de usar .delete()
        profesionalExistente.setEstado(false);
        profesionalRepository.save(profesionalExistente);

        return ResponseEntity.ok(Map.of(
                "mensaje",
                "El profesional ha sido desactivado correctamente. No se podrán agendar nuevas citas con él, pero se conserva su historial."));
    }
}
