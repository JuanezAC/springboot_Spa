package com.proyect.final_proyect_spa4.controllers;

import com.proyect.final_proyect_spa4.services.ProfesionalService;
import com.proyect.final_proyect_spa4.services.SesionService;

import jakarta.servlet.http.HttpSession;

import com.proyect.final_proyect_spa4.entities.Profesional;


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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("api/profesionales")
public class ProfesionalController {
    private final ProfesionalService profesionalService;
    private final SesionService sesionService;

    public ProfesionalController(ProfesionalService profesionalService, SesionService sesionService) {
        this.profesionalService = profesionalService;
        this.sesionService = sesionService;
    }

    @GetMapping
    public ResponseEntity<?> buscarTodosProfesionales() {
        return ResponseEntity.ok(profesionalService.buscarTodosProfesionales());
    }

    // GET /api/profesionales/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarProfesionalPorId(@PathVariable Long id) {
        Profesional profesional = profesionalService.buscarProfesionalPorId(id);
        
        if (profesional == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "Profesional no encontrado con el ID "));
        }
        
        return ResponseEntity.ok(profesional);
    }

    // GET /api/profesionales/servicio/{servicioId}
    @GetMapping("/servicio/{servicioId}")
    public ResponseEntity<?> buscarProfesionalesPorServicio(@PathVariable Long servicioId) {
        List<Profesional> profesionales = profesionalService.buscarProfesionalesPorServicio(servicioId);
        
        if (profesionales.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "No hay profesionales que ofrezcan este servicio"));
        }
        
        return ResponseEntity.ok(profesionales);
    }

    @PostMapping
    public ResponseEntity<?> guardarProfesional(@RequestBody Profesional profesional, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debe iniciar sesión"));
        }
        
        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "Acceso denegado: Se requieren permisos de administrador"));
        }

        try {
            return profesionalService.guardarProfesional(profesional);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "mensaje", "Error interno al intentar registrar el profesional",
                    "error", e.getMessage()
                ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProfesional(@PathVariable Long id, @RequestBody Profesional profesional, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debe iniciar sesión"));
        }
        
        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "No tienes permisos para mosdificar profesionales"));
        }

        try {
            return profesionalService.actualizarProfesional(id, profesional);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "mensaje", "Ocurrió un error inesperado al actualizar el profesional",
                    "error", e.getMessage()
                ));
        }
    }
    
    // DELETE /api/profesionales/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProfesional(@PathVariable Long id, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", "No tiene permisos para eliminar profesionales"));
        }

        try {
            // El servicio ahora maneja el borrado lógico (activo = false)
            return profesionalService.eliminarProfesional(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al intentar eliminar el profesional", "error", e.getMessage()));
        }
    }
}