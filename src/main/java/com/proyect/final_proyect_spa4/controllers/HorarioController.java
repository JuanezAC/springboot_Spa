package com.proyect.final_proyect_spa4.controllers;

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

import com.proyect.final_proyect_spa4.entities.HorarioDisponible;
import com.proyect.final_proyect_spa4.services.HorarioService;
import com.proyect.final_proyect_spa4.services.SesionService;
import com.proyect.final_proyect_spa4.services.SseService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    private final HorarioService horarioService;
    private final SesionService sesionService;
    private final SseService sseService;

    public HorarioController(HorarioService horarioService, SesionService sesionService, SseService sseService) {
        this.horarioService = horarioService;
        this.sesionService = sesionService;
        this.sseService = sseService;
    }

    @GetMapping
    public ResponseEntity<?> buscarTodosHorarios() {
        try {
            return ResponseEntity.ok(horarioService.buscarTodosHorarios());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al cargar horarios", "error", e.getMessage()));
        }
    }

    @GetMapping("/disponibles")
    public ResponseEntity<?> buscarHorariosDisponibles() {
        try {
            return ResponseEntity.ok(horarioService.buscarHorariosDisponibles());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al cargar horarios disponibles", "error", e.getMessage()));
        }
    }

    @GetMapping("/profesional/{profesionalId}")
    public ResponseEntity<?> buscarHorariosPorProfesional(@PathVariable Long profesionalId) {
        return ResponseEntity.ok(horarioService.buscarHorariosPorProfesional(profesionalId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarHorarioPorId(@PathVariable Long id) {
        HorarioDisponible horario = horarioService.buscarHorarioPorId(id);

        if (horario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Horario no encontrado"));
        }

        return ResponseEntity.ok(horario);
    }

    @PostMapping
    public ResponseEntity<?> guardarHorario(@RequestBody HorarioDisponible horario, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", "No tiene permisos para crear horarios"));
        }

        ResponseEntity<?> respuesta = horarioService.guardarHorario(horario);
        if (respuesta.getStatusCode().is2xxSuccessful()) {
            sseService.enviarEvento("HORARIOS_ACTUALIZADOS");
        }
        return respuesta;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarHorario(@PathVariable Long id, @RequestBody HorarioDisponible horario, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", "No tiene permisos para actualizar horarios"));
        }

        ResponseEntity<?> respuesta = horarioService.actualizarHorario(id, horario);
        if (respuesta.getStatusCode().is2xxSuccessful()) {
            sseService.enviarEvento("HORARIOS_ACTUALIZADOS");
        }
        return respuesta;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarHorario(@PathVariable Long id, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Debe iniciar sesión");
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)//
                .body("No tiene permisos para eliminar horarios");
        }

        Boolean eliminar = horarioService.eliminarHorario(id);

        if (!eliminar) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Horario no encontrado");
        }

        sseService.enviarEvento("HORARIOS_ACTUALIZADOS");
        return ResponseEntity.ok("Horario eliminado con exito");
    }
}
