package com.proyecto.spa_1.Services;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proyecto.spa_1.Entities.Profesional;
import com.proyecto.spa_1.Entities.ProfesionalServicio;
import com.proyecto.spa_1.Entities.Servicio;
import com.proyecto.spa_1.Rpositories.ProSerRepository;
import com.proyecto.spa_1.Rpositories.ProfesionalRepository;
import com.proyecto.spa_1.Rpositories.ServicioRepository;

@Service
public class ProSerService {
    private final ProSerRepository proSerRepository;
    private final ProfesionalRepository profesionalRepository;
    private final ServicioRepository servicioRepository;

    public ProSerService(ProSerRepository proSerRepository, ProfesionalRepository profesionalRepository, ServicioRepository servicioRepository) {
        this.proSerRepository = proSerRepository;
        this.profesionalRepository = profesionalRepository;
        this.servicioRepository = servicioRepository;
    }

    public List<ProfesionalServicio> obtenerTodos() {
        return proSerRepository.findAll();
    }

    public ProfesionalServicio obtenerPorId(Long id) {
        return proSerRepository.findById(id).orElse(null);
    }

    public List<ProfesionalServicio> obtenerPorProfesional(Long profesionalId) {
        return proSerRepository.findByProfesionalId(profesionalId);
    }

    public List<ProfesionalServicio> obtenerPorServicio(Long servicioId) {
        return proSerRepository.findByServicioId(servicioId);
    }

    public ResponseEntity<?> guardar(ProfesionalServicio proSer) {
        // Validaciones de IDs obligatorios
        if (proSer.getProfesional() == null || proSer.getProfesional().getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El id del profesional es obligatorio"));
        }

        if (proSer.getServicio() == null || proSer.getServicio().getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El id del servicio es obligatorio"));
        }

        Long profesionalId = proSer.getProfesional().getId();
        Long servicioId = proSer.getServicio().getId();

        Profesional profesional = profesionalRepository.findById(profesionalId).orElse(null);
        Servicio servicio = servicioRepository.findById(servicioId).orElse(null);

        // Verificación de existencia
        if (profesional == null || servicio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "Profesional no encontrado con id o servicio no encontrado con id"));
        }

        // REGLA DE NEGOCIO: El profesional debe estar activo 
        if (!profesional.getActivo()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "No se puede asignar servicios a un profesional inactivo"));
        }

        // Verificación de duplicados
        if (proSerRepository.existsByProfesionalIdAndServicioId(profesionalId, servicioId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", "El profesional ya ofrece este servicio"));
        }

        ProfesionalServicio nuevoProfesionalServicio = new ProfesionalServicio();
        nuevoProfesionalServicio.setProfesional(profesional);
        nuevoProfesionalServicio.setServicio(servicio);

        return ResponseEntity.status(HttpStatus.CREATED).body(proSerRepository.save(nuevoProfesionalServicio));
    }

    public ResponseEntity<?> actualizar(Long id, ProfesionalServicio proSerActualizada) {
        ProfesionalServicio proSerExistente = obtenerPorId(id);

        if (proSerExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "No se ha encontrado el profesional-servicio con el id"));
        }

        if (proSerActualizada.getProfesional() == null || proSerActualizada.getProfesional().getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El ID del profesional es obligatorio para actualizar."));
        }

        if (proSerActualizada.getServicio() == null || proSerActualizada.getServicio().getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El ID del servicio es obligatorio para actualizar."));
        }

        Profesional profesional = profesionalRepository.findById(proSerActualizada.getProfesional().getId()).orElse(null);
        Servicio servicio = servicioRepository.findById(proSerActualizada.getServicio().getId()).orElse(null);

        if (profesional == null || servicio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "Profesional o Servicio no encontrados para la actualización"));
        }

        proSerExistente.setProfesional(profesional);
        proSerExistente.setServicio(servicio);

        return ResponseEntity.ok(proSerRepository.save(proSerExistente));
    }

    public ResponseEntity<?> eliminar(Long id) {
        ProfesionalServicio proSerExistente = obtenerPorId(id);

        if (proSerExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "Profesional-Servicio no encontrado para eliminar"));
        }

        proSerRepository.delete(proSerExistente);
        return ResponseEntity.ok(Map.of("mensaje", "Relación eliminada correctamente"));
    }
}