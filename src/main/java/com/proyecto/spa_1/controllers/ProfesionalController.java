package com.proyecto.spa_1.controllers;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.proyecto.spa_1.Entities.Profesional;
import com.proyecto.spa_1.Services.ProfesionalService;
import com.proyecto.spa_1.Services.SesionService;
import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
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
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(profesionalService.obtenerTodos());
    }

    // GET /api/profesionales/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Profesional profesional = profesionalService.obtenerPorId(id);
        
        if (profesional == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "Profesional no encontrado con el ID "));
        }
        
        return ResponseEntity.ok(profesional);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Profesional profesional, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debe iniciar sesión"));
        }
        
        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "Acceso denegado: Se requieren permisos de administrador"));
        }

        try {
            return profesionalService.guardar(profesional);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "mensaje", "Error interno al intentar registrar el profesional",
                    "error", e.getMessage()
                ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Profesional profesional, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debe iniciar sesión"));
        }
        
        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "No tienes permisos para mosdificar profesionales"));
        }

        try {
            return profesionalService.actualizar(id, profesional);
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
    public ResponseEntity<?> eliminar(@PathVariable Long id, HttpSession session) {
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
            return profesionalService.eliminar(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al intentar eliminar el profesional", "error", e.getMessage()));
        }
    }
}
