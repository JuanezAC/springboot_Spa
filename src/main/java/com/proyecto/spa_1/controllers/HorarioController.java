package com.proyecto.spa_1.controllers;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.proyecto.spa_1.Entities.HorarioDisponible;
import com.proyecto.spa_1.Services.HorarioService;
import com.proyecto.spa_1.Services.SesionService;
import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("api/horarios")
public class HorarioController {
    private final HorarioService horarioService;
    private final SesionService sesionService;

    public HorarioController(HorarioService horarioService, SesionService sesionService) {
        this.horarioService = horarioService;
        this.sesionService = sesionService;
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        return ResponseEntity.ok(horarioService.obtenerTodos());
    }

    //Duda de como buscar por profesional, si es por id o por objeto completo
    @GetMapping("/profesional/{id}")
    public ResponseEntity<?> obtenerPorProfesional(@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.obtenerPorProfesional(id));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody HorarioDisponible horario, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debes iniciar sesión para crear horarios"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "Solo el administrador puede crear horarios"));
        }
        try {
            return horarioService.guardar(horario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al crear el horario", "error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody HorarioDisponible horario, HttpSession session){
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debes iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "Solo el administrador puede actualizar horarios"));
        }

        try {
            return horarioService.actualizar(id, horario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al actualizar el horario", "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debes iniciar sesión para eliminar horarios"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "Acceso denegado"));
        }
        try {
            return horarioService.eliminar(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al eliminar horario", "error", e.getMessage()));
        }
    }
}