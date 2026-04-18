package com.proyect.final_proyect_spa4.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyect.final_proyect_spa4.services.CitaService;
import com.proyect.final_proyect_spa4.entities.Cita;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;
    
    public CitaController(CitaService citaService){
        this.citaService = citaService;
    }
    @GetMapping("/{id}")
        public ResponseEntity<?> obtenerId(@PathVariable Long id){
            Cita cita = citaService.obtenerId(id);

            if(cita == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("Mensaje", "La cita no fue encontrada"));
            }
            return ResponseEntity.ok(cita);
        }

        @PostMapping
            public ResponseEntity<Cita>guardar(@RequestBody Cita cita){
                Cita nuevaCita = citaService.guardar(cita);
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCita);
            }
                }
                
        
        


