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

import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/profesional-servicios")
public class ProSerController {
    
    private final ProSerService proSerService;
    private final SesionService sesionService;

    public ProSerController(ProSerService proSerService, SesionService sesionService) {
        this.proSerService = proSerService;
        this.sesionService = sesionService;
    }

    // Listar todas las relaciones
    @GetMapping
    public ResponseEntity<List<ProfesionalServicio>> listarTodas() {
        return proSerService.obtenerTodos();
    }

    // Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return proSerService.obtenerPorId(id);
    }

    // Obtener servicios de un profesional
    @GetMapping("/profesional/{profesionalId}")
    public ResponseEntity<List<ProfesionalServicio>> obtenerPorProfesional(@PathVariable Long profesionalId) {
        return proSerService.obtenerPorProfesional(profesionalId);
    }

    // Obtener profesionales de un servicio
    @GetMapping("/servicio/{servicioId}")
    public ResponseEntity<List<ProfesionalServicio>> obtenerPorServicio(@PathVariable Long servicioId) {
        return proSerService.obtenerPorServicio(servicioId);
    }

    // Guardar relación
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ProfesionalServicio proSer, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Acceso denegado: Se requieren permisos de administrador"));
        }

        try {
            return proSerService.guardar(proSer);
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
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ProfesionalServicio proSer, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Acceso denegado: Se requieren permisos de administrador"));
        }

        try {
            return proSerService.actualizar(id, proSer);
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
    public ResponseEntity<?> eliminar(@PathVariable Long id, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Acceso denegado: Se requieren permisos de administrador"));
        }

        try {
            return proSerService.eliminar(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Error al eliminar la relación",
                    "detalles", e.getMessage()
                ));
        }
    }
}
