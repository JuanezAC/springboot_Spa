package com.proyecto.spa_1.Services;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.proyecto.spa_1.Entities.Servicio;
import com.proyecto.spa_1.Rpositories.CitaRepository;
import com.proyecto.spa_1.Rpositories.ProSerRepository;
import com.proyecto.spa_1.Rpositories.ServicioRepository;

@Service
public class ServicioService {
    private final ServicioRepository servicioRepository;
    private final ProSerRepository proSerRepository;
    private final CitaRepository citaRepository;

    public ServicioService(ServicioRepository servicioRepository, ProSerRepository proSerRepository, CitaRepository citaRepository) {
        this.servicioRepository = servicioRepository;
        this.proSerRepository = proSerRepository;
        this.citaRepository = citaRepository;
    }

    public List<Servicio> obtenerTodos() {
        return servicioRepository.findAll();
    }

    public Servicio obtenerPorId(Long id) {
        return servicioRepository.findById(id).orElse(null);
    }

    public ResponseEntity<?> guardar(Servicio servicio) {
        if (servicio.getId() != null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "No se debe enviar el ID al registrar un servicio"));
        }

        // Validación: Nombre
        if (servicio.getNombre() == null || servicio.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El nombre del servicio es obligatorio"));
        }

        // REGLA DE NEGOCIO: Duración > 0
        if (servicio.getDuracion() == null || servicio.getDuracion() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "La duración debe ser mayor a cero minutos"));
        }

        // REGLA DE NEGOCIO: Precio > 0
        if (servicio.getPrecio() == null || servicio.getPrecio() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El precio debe ser un valor positivo"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(servicioRepository.save(servicio));
    }

    public ResponseEntity<?> actualizar(Long id, Servicio servicioActualizado) {
        Servicio servicioExistente = obtenerPorId(id);

        if (servicioExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Servicio no encontrado"));
        }

        if (servicioActualizado.getNombre() != null) {
            if (servicioActualizado.getNombre().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "Nombre no puede estar vacio"));
            }
            servicioExistente.setNombre(servicioActualizado.getNombre().trim());
        }

        // Validación de Duración en actualización
        if (servicioActualizado.getDuracion() != null) {
            if (servicioActualizado.getDuracion() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "La duración debe ser mayor a cero"));
            }
            servicioExistente.setDuracion(servicioActualizado.getDuracion());
        }

        // Validación de Precio en actualización
        if (servicioActualizado.getPrecio() != null) {
            if (servicioActualizado.getPrecio() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "El precio debe ser mayor a cero"));
            }
            servicioExistente.setPrecio(servicioActualizado.getPrecio());
        }

        if (servicioActualizado.getDescripcion() != null) {
            if (servicioActualizado.getDescripcion().isBlank()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "La descripción no puede estar vacía si decides enviarla"));
            }
            servicioExistente.setDescripcion(servicioActualizado.getDescripcion().trim());
        }

        return ResponseEntity.ok(servicioRepository.save(servicioExistente));
    }

    public ResponseEntity<?> eliminar(Long id) {
        Servicio servicio = obtenerPorId(id);
        if (servicio == null) return ResponseEntity.notFound().build();

        // ✅ Validaciones claras y específicas
        long asignaciones = proSerRepository.countByServicioId(id);
        long citas = citaRepository.countByServicioId(id);

        if (asignaciones > 0 || citas > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensaje", 
                    "No se puede eliminar: tiene " + asignaciones + " asignación(es) y \n" + citas + " cita(s)."));
        }

        servicioRepository.delete(servicio);
        return ResponseEntity.ok(Map.of("mensaje", "Servicio eliminado correctamente"));
    }
}
