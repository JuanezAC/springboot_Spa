package com.proyect.final_proyect_spa4.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyect.final_proyect_spa4.entities.ProfesionalServicio;
import com.proyect.final_proyect_spa4.services.ProSerService;
import com.proyect.final_proyect_spa4.services.SesionService;
import com.proyect.final_proyect_spa4.services.SseService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/profesional-servicios")
public class ProSerController {
    
    private final ProSerService proSerService;
    private final SesionService sesionService;
    private final SseService sseService;

    public ProSerController(ProSerService proSerService, SesionService sesionService, SseService sseService) {
        this.proSerService = proSerService;
        this.sesionService = sesionService;
        this.sseService = sseService;
    }

    // Listar todas las relaciones
    @GetMapping
    public ResponseEntity<?> buscarTodosProSer() {
        try {
            return proSerService.buscarTodosProSer();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al cargar asignaciones", "detalles", e.getMessage()));
        }
    }

    // Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarProSerPorId(@PathVariable Long id) {
        return proSerService.buscarProSerPorId(id);
    }

    // Obtener servicios de un profesional
    @GetMapping("/profesional/{profesionalId}")
    public ResponseEntity<List<ProfesionalServicio>> buscarProSerPorProfesional(@PathVariable Long profesionalId) {
        return proSerService.buscarProSerPorProfesional(profesionalId);
    }

    // Obtener profesionales de un servicio
    @GetMapping("/servicio/{servicioId}")
    public ResponseEntity<List<ProfesionalServicio>> buscarProSerPorServicio(@PathVariable Long servicioId) {
        return proSerService.buscarProSerPorServicio(servicioId);
    }

    // Guardar relación
    @PostMapping
    public ResponseEntity<?> guardarProSer(@RequestBody ProfesionalServicio proSer, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Acceso denegado: Se requieren permisos de administrador"));
        }

        try {
            ResponseEntity<?> respuesta = proSerService.guardarProSer(proSer);
            if (respuesta.getStatusCode().is2xxSuccessful()) {
                sseService.enviarEvento("PROFESIONALES_ACTUALIZADOS");
            }
            return respuesta;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Error al registrar la relación",
                    "detalles", e.getMessage()
                ));
        }
    }

    // Actualizar relación
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProSer(@PathVariable Long id, @RequestBody ProfesionalServicio proSer, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Acceso denegado: Se requieren permisos de administrador"));
        }

        try {
            ResponseEntity<?> respuesta = proSerService.actualizarProSer(id, proSer);
            if (respuesta.getStatusCode().is2xxSuccessful()) {
                sseService.enviarEvento("PROFESIONALES_ACTUALIZADOS");
            }
            return respuesta;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Error al actualizar la relación",
                    "detalles", e.getMessage()
                ));
        }
    }

    // Eliminar relación
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProSer(@PathVariable Long id, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Acceso denegado: Se requieren permisos de administrador"));
        }

        try {
            ResponseEntity<?> respuesta = proSerService.eliminarProSer(id);
            if (respuesta.getStatusCode().is2xxSuccessful()) {
                sseService.enviarEvento("PROFESIONALES_ACTUALIZADOS");
            }
            return respuesta;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Error al eliminar la relación",
                    "detalles", e.getMessage()
                ));
        }
    }
}
