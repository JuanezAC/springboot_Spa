package com.proyecto.spa_1.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.spa_1.Entities.ProfesionalServicio;
import com.proyecto.spa_1.Services.ProSerService;
import com.proyecto.spa_1.Services.SesionService;

import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/proSer")
public class ProSerController {

    private final ProSerService proSerService;
    private final SesionService sesionService;

    public ProSerController(ProSerService proSerService, SesionService sesionService) {
        this.proSerService = proSerService;
        this.sesionService = sesionService;
    }

    @GetMapping
    public ResponseEntity<List<ProfesionalServicio>> obtenerTodos() {
        return ResponseEntity.ok(proSerService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        ProfesionalServicio proSer = proSerService.obtenerPorId(id);

        if (proSer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Asignación profesional-servicio no encontrada"));
        }
        return ResponseEntity.ok(proSer);
    }

    @GetMapping("/profesional/{profesionalId}")
    public ResponseEntity<List<ProfesionalServicio>> obtenerPorProfesional(@PathVariable Long profesionalId) {
        return ResponseEntity.ok(proSerService.obtenerPorProfesional(profesionalId));
    }

    @GetMapping("/servicio/{servicioId}")
    public ResponseEntity<List<ProfesionalServicio>> obtenerPorServicio(@PathVariable Long servicioId) {
        return ResponseEntity.ok(proSerService.obtenerPorServicio(servicioId));
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody ProfesionalServicio proSer, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("mensaje", "Acceso denegado: Se requieren permisos de administrador"));
        }

        try {
            return proSerService.guardar(proSer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "Error al crear la asignación", "error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ProfesionalServicio proSer, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("mensaje", "No tiene permisos para modificar asignaciones"));
        }

        try {
            return proSerService.actualizar(id, proSer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "Error al actualizar la asignación", "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("mensaje", "No tiene permisos para eliminar asignaciones"));
        }

        try {
            return proSerService.eliminar(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Ocurrió un error al intentar eliminar", "error", e.getMessage()));
        }
    }
}