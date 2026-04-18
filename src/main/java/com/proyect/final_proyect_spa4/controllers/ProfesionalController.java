package com.proyect.final_proyect_spa4.controllers;

import com.proyect.final_proyect_spa4.services.ProfesionalService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/profesional")
public class ProfesionalController {
    private final ProfesionalService profesionalService;

    public ProfesionalController(ProfesionalService profesionalService) {
    this.profesionalService = profesionalService;
    
    }
    //Listar todos
    @GetMapping
    public ResponseEntity<List<Profesional>> obtener(){
        return ResponseEntity.ok(profesionalService.obtener());
    }
    //Obtener por id y revisar si existen
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerId(@PathVariable Long id){
        Profesional profesional = profesionalService.obtenerId(id);

        if(profesional == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("Mensaje", "No se encontro ningun trabajador bajo esa identificacion"));
        }
        return ResponseEntity.ok(profesional);
    }
    //Guardar
    @PostMapping
    public ResponseEntity<Profesional> guardar(@RequestBody Profesional profesional){
        Profesional nuevoProfesional = profesionalService.guardar(profesional);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProfesional);
    }
    //Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<?>Actualizar(@PathVariable Long id, @RequestBody Profesional profesional){
        Profesional profesionalActualizado = profesionalService.actualizar(id, profesional);

        if(profesionalActualizado == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("Mensaje", "No se encontro el profesional"));
        }
        return ResponseEntity.ok(profesionalActualizado);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Profesional profesionalEliminado = profesionalService.eliminar(id);

        if(profesionalEliminado == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("Mensaje", "No se encontro el profesional"));
        }
        return ResponseEntity.ok(profesionalEliminado);
    }
}


