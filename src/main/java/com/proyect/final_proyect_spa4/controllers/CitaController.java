package com.proyect.final_proyect_spa4.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyect.final_proyect_spa4.services.CitaService;
import com.proyect.final_proyect_spa4.services.SesionService;

import jakarta.servlet.http.HttpSession;

import com.proyect.final_proyect_spa4.entities.Cita;
import com.proyect.final_proyect_spa4.entities.UsuarioSesion;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;
    private final SesionService sesionService;

    public CitaController(CitaService citaService, SesionService sesionService) {
        this.citaService = citaService;
        this.sesionService = sesionService;
    }

    // GET /api/citas (Solo Admin)
    @GetMapping
    public ResponseEntity<?> obtenerTodas(HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debes iniciar sesión para consultar citas"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "No tienes permisos de administrador para ver detalles de citas ajenas"));
        }
        return ResponseEntity.ok(citaService.obtenerTodos());
    }

    // GET /api/citas/{id} (Solo Admin)
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debes iniciar sesión para consultar citas"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", "No tienes permisos de administrador para ver detalles de citas ajenas"));
        }

        Cita cita = citaService.obtenerPorId(id);

        if (cita == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "La cita con ID " + id + " no existe en el sistema"));
        }

        return ResponseEntity.ok(cita);
    }


    // GET /api/citas/mis-citas
    @GetMapping("/mis-citas")
    public ResponseEntity<?> obtenerMisCitas(HttpSession session) {
        // Obtener el usuario de la sesión
        UsuarioSesion usuario = sesionService.obtenerUsuario(session);
        
        // Validar si existe sesión activa
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Sesión expirada o no válida. Por favor, inicia sesión nuevamente. Tien que estar con la sesion activa"));
        }
        
        return ResponseEntity.ok(citaService.obtenerPorUsuario(usuario.getId()));
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Cita cita, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debes iniciar sesión para agendar"));
        }
        try {
            return citaService.guardar(cita);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al agendar la cita", "error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Cita cita, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Sesión inválida"));
        }
        try {
            return citaService.actualizar(id, cita);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al actualizar la cita", "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Sesión inválida"));
        }
        try {
            return citaService.eliminar(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al cancelar la cita", "error", e.getMessage()));
        }
    }
}


