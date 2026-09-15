package com.proyect.final_proyect_spa4.controllers;

import com.proyect.final_proyect_spa4.services.ProfesionalService;
import com.proyect.final_proyect_spa4.services.SesionService;
import com.proyect.final_proyect_spa4.services.SseService;

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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/profesionales")
public class ProfesionalController {
    private final ProfesionalService profesionalService;
    private final SesionService sesionService;
    private final SseService sseService;

    public ProfesionalController(ProfesionalService profesionalService, SesionService sesionService, SseService sseService) {
        this.profesionalService = profesionalService;
        this.sesionService = sesionService;
        this.sseService = sseService;
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
            ResponseEntity<?> respuesta = profesionalService.guardarProfesional(profesional);
            if (respuesta.getStatusCode().is2xxSuccessful()) {
                sseService.enviarEvento("PROFESIONALES_ACTUALIZADOS");
            }
            return respuesta;
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
            ResponseEntity<?> respuesta = profesionalService.actualizarProfesional(id, profesional);
            if (respuesta.getStatusCode().is2xxSuccessful()) {
                sseService.enviarEvento("PROFESIONALES_ACTUALIZADOS");
            }
            return respuesta;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "mensaje", "Ocurrió un error inesperado al actualizar el profesional",
                    "error", e.getMessage()
                ));
        }
    }
    
    // GET /api/profesionales/{id}/info-eliminacion
    @GetMapping("/{id}/info-eliminacion")
    public ResponseEntity<?> obtenerInfoEliminacion(@PathVariable Long id, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debe iniciar sesión"));
        }
        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", "No tiene permisos"));
        }
        Map<String, Object> info = profesionalService.obtenerInfoEliminacion(id);
        if (info == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "Profesional no encontrado"));
        }
        return ResponseEntity.ok(info);
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
            ResponseEntity<?> respuesta = profesionalService.eliminarProfesional(id);
            if (respuesta.getStatusCode().is2xxSuccessful()) {
                sseService.enviarEvento("PROFESIONALES_ACTUALIZADOS");
            }
            return respuesta;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al intentar eliminar el profesional", "error", e.getMessage()));
        }
    }
}