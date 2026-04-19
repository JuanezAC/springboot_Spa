package com.proyect.final_proyect_spa4.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proyect.final_proyect_spa4.entities.HorarioDisponible;
import com.proyect.final_proyect_spa4.repositories.HorarioRepository;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;

    public HorarioService(HorarioRepository horarioRepository){
        this.horarioRepository = horarioRepository;
    }

    public List<HorarioDisponible> obtenerHorario(){
        return horarioRepository.findAll();
    }

    public List<HorarioDisponible> obtenerHorarioPorProfesional(Long profesionalId){
        return horarioRepository.findByProfesionalId(profesionalId);
    }

    public HorarioDisponible obtenerPorId(Long id){
        return horarioRepository.findById(id).orElse(null);
    }
    
    public ResponseEntity<?> guardarHorario(HorarioDisponible horario){
        // Validación: Un horario pertenece a un profesional
        if(horario.getProfesional() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "El horario debe pertenecer a un profesional"));
        }

        // Validación: No se pueden registrar horarios en fechas pasadas
        if(horario.getFecha().isBefore(LocalDate.now())){

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "No se pueden registrar horarios en fechas pasadas"));
        }

        // Validación: No se puede reservar un horario que no esté disponible
        if(!horario.getDisponible()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "No se puede registrar un horario que no esta disponible"));
        }

        HorarioDisponible nuevoHorario = horarioRepository.save(horario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHorario);
    }

    public ResponseEntity<?> actualizarHorario(Long id, HorarioDisponible horarioActualizado){
        HorarioDisponible horarioExistente = obtenerPorId(id);

        if(horarioExistente == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Horario no encontrado"));
        }

        // Validación: Un horario pertenece a un profesional
        if(horarioActualizado.getProfesional() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "El horario debe pertenecer a un profesional"));
        }

        // Validación: No se pueden registrar horarios en fechas pasadas
        if(horarioActualizado.getFecha().isBefore(LocalDate.now())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "No se pueden registrar horarios en fechas pasadas"));
        }

        // Validación: No se puede reservar un horario que no esté disponible
        if(!horarioActualizado.getDisponible()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "No se puede registrar un horario que no esta disponible"));
        }

        horarioExistente.setFecha(horarioActualizado.getFecha());
        horarioExistente.setHora(horarioActualizado.getHora());
        horarioExistente.setDisponible(horarioActualizado.getDisponible());
        horarioExistente.setProfesional(horarioActualizado.getProfesional());

        HorarioDisponible actualizado = horarioRepository.save(horarioExistente);
        return ResponseEntity.ok(actualizado);
    }

    public Boolean eliminarHorario(Long id){
        HorarioDisponible horarioExistente = obtenerPorId(id);

        if(horarioExistente == null){
            return false;
        }

        horarioRepository.delete(horarioExistente);
        return true;
    }

}
