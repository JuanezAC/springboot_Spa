package com.proyect.final_proyect_spa4.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyect.final_proyect_spa4.entities.Cita;
import com.proyect.final_proyect_spa4.entities.HorarioDisponible;
import com.proyect.final_proyect_spa4.entities.Profesional;
import com.proyect.final_proyect_spa4.entities.ProfesionalServicio;
import com.proyect.final_proyect_spa4.entities.Servicio;
import com.proyect.final_proyect_spa4.repositories.CitaRepository;
import com.proyect.final_proyect_spa4.repositories.HorarioRepository;
import com.proyect.final_proyect_spa4.repositories.ProSerRepository;
import com.proyect.final_proyect_spa4.repositories.ProfesionalRepository;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;
    private final CitaRepository citaRepository;
    private final ProfesionalRepository profesionalRepository;
    private final ProSerRepository proSerRepository;

    public HorarioService(HorarioRepository horarioRepository, CitaRepository citaRepository,
            ProfesionalRepository profesionalRepository, ProSerRepository proSerRepository) {
        this.horarioRepository = horarioRepository;
        this.citaRepository = citaRepository;
        this.profesionalRepository = profesionalRepository;
        this.proSerRepository = proSerRepository;
    }

    @Transactional
    public List<HorarioDisponible> buscarTodosHorarios() {
        return horarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<HorarioDisponible> buscarHorariosDisponibles() {
        return horarioRepository.findDisponibles();
    }

    public List<HorarioDisponible> buscarHorariosPorProfesional(Long profesionalId) {
        return horarioRepository.findByProfesionalId(profesionalId);
    }

    public HorarioDisponible buscarHorarioPorId(Long id) {
        return horarioRepository.findById(id).orElse(null);
    }

    public ResponseEntity<?> guardarHorario(HorarioDisponible horario) {
        if (horario.getProfesional() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "El horario debe pertenecer a un profesional"));
        }

        if (horario.getFecha().isBefore(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "No se pueden registrar horarios en fechas pasadas"));
        }

        if (!horario.getDisponible()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "No se puede registrar un horario que no esta disponible"));
        }

        HorarioDisponible nuevoHorario = horarioRepository.save(horario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHorario);
    }

    @Transactional
    public ResponseEntity<?> actualizarHorario(Long id, HorarioDisponible horarioActualizado) {
        HorarioDisponible horarioExistente = buscarHorarioPorId(id);

        if (horarioExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Horario no encontrado"));
        }

        if (horarioActualizado.getProfesional() == null || horarioActualizado.getProfesional().getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "El horario debe pertenecer a un profesional"));
        }

        if (horarioActualizado.getFecha() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "La fecha es obligatoria"));
        }

        if (horarioActualizado.getHora() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "La hora es obligatoria"));
        }

        if (horarioActualizado.getDisponible() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "El campo disponible es obligatorio"));
        }

        Profesional nuevoProfesional = profesionalRepository.findById(horarioActualizado.getProfesional().getId()).orElse(null);
        if (nuevoProfesional == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "El profesional especificado no existe"));
        }

        Optional<Cita> citaExistenteOpt = citaRepository.findByProfesionalIdAndFechaAndHora(
                horarioExistente.getProfesional().getId(),
                horarioExistente.getFecha(),
                horarioExistente.getHora());

        if (citaExistenteOpt.isEmpty()) {
            return actualizarSinCita(horarioExistente, horarioActualizado, nuevoProfesional);
        }

        Cita citaExistente = citaExistenteOpt.get();
        return actualizarConCita(horarioExistente, horarioActualizado, nuevoProfesional, citaExistente);
    }

    private ResponseEntity<?> actualizarSinCita(HorarioDisponible horarioExistente,
            HorarioDisponible horarioActualizado, Profesional nuevoProfesional) {
        horarioExistente.setFecha(horarioActualizado.getFecha());
        horarioExistente.setHora(horarioActualizado.getHora());
        horarioExistente.setDisponible(horarioActualizado.getDisponible());
        horarioExistente.setProfesional(nuevoProfesional);

        HorarioDisponible actualizado = horarioRepository.save(horarioExistente);
        return ResponseEntity.ok(actualizado);
    }

    private ResponseEntity<?> actualizarConCita(HorarioDisponible horarioExistente,
            HorarioDisponible horarioActualizado, Profesional nuevoProfesional, Cita citaExistente) {

        boolean cambiandoDisponibilidad = horarioExistente.getDisponible()
                && !horarioActualizado.getDisponible();
        boolean cambiandoProfesional = !horarioExistente.getProfesional().getId()
                .equals(horarioActualizado.getProfesional().getId());
        boolean cambiandoFecha = !horarioExistente.getFecha().equals(horarioActualizado.getFecha());
        boolean cambiandoHora = !horarioExistente.getHora().equals(horarioActualizado.getHora());

        if (cambiandoDisponibilidad) {
            horarioExistente.setFecha(horarioActualizado.getFecha());
            horarioExistente.setHora(horarioActualizado.getHora());
            horarioExistente.setDisponible(false);
            horarioExistente.setProfesional(horarioActualizado.getProfesional());
            horarioRepository.save(horarioExistente);
            citaRepository.delete(citaExistente);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Horario desactivado y cita cancelada exitosamente"));
        }

        if (cambiandoProfesional) {
            Long servicioId = citaExistente.getServicio().getId();
            boolean nuevoProfesionalTieneServicio = proSerRepository
                    .findByProfesionalId(nuevoProfesional.getId()).stream()
                    .anyMatch(ps -> ps.getServicio().getId().equals(servicioId));

            if (!nuevoProfesionalTieneServicio) {
                Servicio servicio = citaExistente.getServicio();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error",
                        "El profesional seleccionado no tiene asignado el servicio '"
                                + servicio.getNombre() + "' de la cita existente"));
            }
        }

        boolean moviendoCita = cambiandoProfesional || cambiandoFecha || cambiandoHora;
        if (moviendoCita) {
            if (horarioActualizado.getFecha().isBefore(LocalDate.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "No se pueden registrar horarios en fechas pasadas"));
            }

            Long nuevoProfesionalId = nuevoProfesional.getId();
            boolean ocupado = citaRepository.existsByProfesionalIdAndFechaAndHora(
                    nuevoProfesionalId,
                    horarioActualizado.getFecha(),
                    horarioActualizado.getHora());

            if (ocupado) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error",
                        "El nuevo horario seleccionado ya está ocupado por otra cita. "
                                + "Por favor elige otra fecha, hora o profesional."));
            }
        }

        horarioExistente.setFecha(horarioActualizado.getFecha());
        horarioExistente.setHora(horarioActualizado.getHora());
        horarioExistente.setDisponible(horarioActualizado.getDisponible());
        horarioExistente.setProfesional(nuevoProfesional);
        horarioRepository.save(horarioExistente);

        if (cambiandoProfesional || cambiandoFecha || cambiandoHora) {
            citaExistente.setProfesional(nuevoProfesional);
            citaExistente.setFecha(horarioActualizado.getFecha());
            citaExistente.setHora(horarioActualizado.getHora());
            citaRepository.save(citaExistente);
        }

        return ResponseEntity.ok(horarioExistente);
    }

    public Boolean eliminarHorario(Long id) {
        HorarioDisponible horarioExistente = buscarHorarioPorId(id);

        if (horarioExistente == null) {
            return false;
        }

        horarioRepository.delete(horarioExistente);
        return true;
    }
}
