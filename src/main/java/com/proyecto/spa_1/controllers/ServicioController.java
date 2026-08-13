package com.proyecto.spa_1.controllers;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.proyecto.spa_1.Entities.Servicio;
import com.proyecto.spa_1.Services.ServicioService;
import com.proyecto.spa_1.Services.SesionService;
import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("api/servicios")
public class ServicioController {
    private final ServicioService servicioService;
    private final SesionService sesionService;

    public ServicioController(ServicioService servicioService, SesionService sesionService) {
        this.servicioService = servicioService;
        this.sesionService = sesionService;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(servicioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Servicio servicio = servicioService.obtenerPorId(id);

        if (servicio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "Servicio no existe"));
        }
        
        return ResponseEntity.ok(servicio);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Servicio servicio, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "No tiene permisos para crear servicios"));
        }
        try {
            return servicioService.guardar(servicio);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al crear servicio", "error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Servicio servicio, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "No tiene permisos para editar servicios"));
        }
        try {
            return servicioService.actualizar(id, servicio);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al actualizar servicio", "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "No tiene permisos para eliminar servicios"));
        }
        try {
            return servicioService.eliminar(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al eliminar servicio"));
        }
    }
}