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

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @GetMapping
    public ResponseEntity<?> obtenerHorario() {
        return ResponseEntity.ok(horarioService.obtenerHorario());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        HorarioDisponible horario = horarioService.obtenerPorId(id);

        if (horario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Horario no encontrado"));
        }

        return ResponseEntity.ok(horario);
    }

    @PostMapping
    public ResponseEntity<?> guardarHorario(@RequestBody HorarioDisponible horario) {
        return horarioService.guardarHorario(horario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarHorario(@PathVariable Long id, @RequestBody HorarioDisponible horario) {
        return horarioService.actualizarHorario(id, horario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarHorario(@PathVariable Long id) {

        Boolean eliminar = horarioService.eliminarHorario(id);

        if (eliminar == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Horario no encontrado");
        }

        return ResponseEntity.ok("Horario eliminado con exito");
    }
}
