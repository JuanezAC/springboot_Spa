package com.proyect.final_proyect_spa4.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proyect.final_proyect_spa4.entities.Servicio;
import com.proyect.final_proyect_spa4.repositories.ServicioRepository;

@Service
public class ServicioService {
    private final ServicioRepository servicioRepository;

    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    public ResponseEntity<?> obtenerTodos() {
        return ResponseEntity.ok(servicioRepository.findAll());
    }

    public ResponseEntity<?> obtenerPorId(Long id) {
        return ResponseEntity.ok(servicioRepository.findById(id));
    }

    public ResponseEntity<?> guardar(Servicio servicio) {
        return ResponseEntity.ok(servicioRepository.save(servicio));
    }

    public ResponseEntity<?> actualizar(Long id, Servicio servicio) {
        Servicio encontrado = servicioRepository.findById(id).orElse(null);

        if (encontrado == null) {
            return ResponseEntity.status(404).body("Servicio no encontrado");
        }

        encontrado.setNombre(servicio.getNombre());
        encontrado.setDescripcion(servicio.getDescripcion());
        encontrado.setDuracion(servicio.getDuracion());
        encontrado.setPrecio(servicio.getPrecio());

        return ResponseEntity.ok(servicioRepository.save(encontrado));
    }

    public ResponseEntity<?> eliminar(Long id) {
        Servicio encontrado = servicioRepository.findById(id).orElse(null);

        if (encontrado == null) {
            return ResponseEntity.status(404).body("Servicio no encontrado");
        }
        servicioRepository.deleteById(id);
        return ResponseEntity.ok(encontrado);
    }
}
