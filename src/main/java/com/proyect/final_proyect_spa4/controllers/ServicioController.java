package com.proyect.final_proyect_spa4.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyect.final_proyect_spa4.entities.Servicio;
import com.proyect.final_proyect_spa4.services.ServicioService;
import com.proyect.final_proyect_spa4.services.SesionService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/servicios")
public class ServicioController {
    private final ServicioService servicioService;
    private final SesionService sesionService;

    public ServicioController(ServicioService servicioService, SesionService sesionService) {
        this.servicioService = servicioService;
        this.sesionService = sesionService;
    }

    @GetMapping()
    public ResponseEntity<?> obtenerTodos() {
        return ResponseEntity.ok(servicioService.obtenerTodos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicioService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Servicio servicio, HttpSession session) {
        // if () {
        //     return ResponseEntity.status(401).body("No hay una sesión activa");
            
        // }

        return ResponseEntity.ok(servicioService.guardar(servicio));
    }
}
