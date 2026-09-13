package com.proyect.final_proyect_spa4.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyect.final_proyect_spa4.entities.Servicio;
import com.proyect.final_proyect_spa4.services.ServicioService;
import com.proyect.final_proyect_spa4.services.SesionService;
import com.proyect.final_proyect_spa4.services.SseService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("api/servicios")
public class ServicioController {
    private final ServicioService servicioService;
    private final SesionService sesionService;
    private final SseService sseService;

    public ServicioController(ServicioService servicioService, SesionService sesionService, SseService sseService) {
        this.servicioService = servicioService;
        this.sesionService = sesionService;
        this.sseService = sseService;
    }

    @GetMapping
    public ResponseEntity<?> buscarTodosServicios() {
        try {
            return ResponseEntity.ok(servicioService.buscarTodosServicios());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al cargar servicios", "error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarServicioPorId(@PathVariable Long id) {
        Servicio servicio = servicioService.buscarServicioPorId(id);

        if (servicio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "Servicio no existe"));
        }
        
        return ResponseEntity.ok(servicio);
    }

    @PostMapping
    public ResponseEntity<?> guardarServicio(@RequestBody Servicio servicio, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "No tiene permisos para crear servicios"));
        }
        try {
            ResponseEntity<?> respuesta = servicioService.guardarServicio(servicio);
            if (respuesta.getStatusCode().is2xxSuccessful()) {
                sseService.enviarEvento("SERVICIOS_ACTUALIZADOS");
            }
            return respuesta;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al crear servicio", "error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarServicio(@PathVariable Long id, @RequestBody Servicio servicio, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "No tiene permisos para editar servicios"));
        }
        try {
            ResponseEntity<?> respuesta = servicioService.actualizarServicio(id, servicio);
            if (respuesta.getStatusCode().is2xxSuccessful()) {
                sseService.enviarEvento("SERVICIOS_ACTUALIZADOS");
            }
            return respuesta;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al actualizar servicio", "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarServicio(@PathVariable Long id, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "No tiene permisos para eliminar servicios"));
        }
        try {
            ResponseEntity<?> respuesta = servicioService.eliminarServicio(id);
            if (respuesta.getStatusCode().is2xxSuccessful()) {
                sseService.enviarEvento("SERVICIOS_ACTUALIZADOS");
            }
            return respuesta;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error al eliminar servicio"));
        }
    }
}
