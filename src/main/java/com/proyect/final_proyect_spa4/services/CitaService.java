package com.proyect.final_proyect_spa4.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proyect.final_proyect_spa4.entities.Cita;
import com.proyect.final_proyect_spa4.entities.Profesional;
import com.proyect.final_proyect_spa4.entities.Servicio;
import com.proyect.final_proyect_spa4.entities.Usuario;
import com.proyect.final_proyect_spa4.repositories.CitaRepository;
import com.proyect.final_proyect_spa4.repositories.ProfesionalRepository;
import com.proyect.final_proyect_spa4.repositories.ServicioRepository;
import com.proyect.final_proyect_spa4.repositories.UsuarioRepository;

@Service
public class CitaService {
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProfesionalRepository profesionalRepository;
    private final ServicioRepository servicioRepository;


    public CitaService(CitaRepository citaRepository, UsuarioRepository usuarioRepository,
            ProfesionalRepository profesionalRepository, ServicioRepository servicioRepository) {
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.profesionalRepository = profesionalRepository;
        this.servicioRepository = servicioRepository;
    }

    public List<Cita> buscarTodasCitas() {
        return citaRepository.findAll();
    }

    public List<Cita> buscarCitasPorUsuario(Long usuarioId) {
        return citaRepository.findByUsuarioId(usuarioId);
    }

    public Cita buscarCitaPorId(Long id) {
        return citaRepository.findById(id).orElse(null);
    }

    public ResponseEntity<?> guardarCita(Cita cita) {
        // 1. Validar que no envíen ID al crear
        if (cita.getId() != null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "No se debe enviar el ID al agendar una nueva cita"));
        }

        // 2. Validar Usuario
        if (cita.getUsuario() == null || cita.getUsuario().getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El usuario es obligatorio para agendar la cita"));
        }

        Long usuarioId = cita.getUsuario().getId();
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "El usuario especificado no existe"));
        }

        // Buscar el profesional
        if (cita.getProfesional() == null || cita.getProfesional().getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El profesional es obligatorio para agendar la cita"));
        }

        Long profesionalId = cita.getProfesional().getId();
        Profesional profesional = profesionalRepository.findById(profesionalId).orElse(null);
        if (profesional == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "El profesional no existe"));
        }

        // 2. REGLA DE NEGOCIO: ¿Está activo?
        if (!profesional.getEstado()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El profesional seleccionado no está disponible actualmente"));
        }

        //Buscar el servicio
        if (cita.getServicio() == null || cita.getServicio().getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Debe elegir un servicio para la cita"));
        }

        Long servicioId = cita.getServicio().getId();
        Servicio servicio = servicioRepository.findById(servicioId).orElse(null);
        if (servicio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "El servicio seleccionado no existe"));
        }


        // 3. Validar Fecha y Hora (No nulos)
        if (cita.getFecha() == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "La fecha de la cita es obligatoria"));
        }
        if (cita.getHora() == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "La hora de la cita es obligatoria"));
        }

        // 4. Lógica de validación de tiempo
        LocalDate hoy = LocalDate.now();
        if (cita.getFecha().isBefore(hoy)) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "No se pueden programar citas en fechas pasadas"));
        }

        if (cita.getFecha().equals(hoy)) {
            if (cita.getHora().isBefore(LocalTime.now())) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "La hora seleccionada ya ha pasado"));
            }
        }

        cita.setUsuario(usuario);
        cita.setProfesional(profesional);
        cita.setServicio(servicio);

        return ResponseEntity.status(HttpStatus.CREATED).body(citaRepository.save(cita));
    }

    public ResponseEntity<?> actualizarCita(Long id, Cita citaActualizada) {
        Cita citaExistente = buscarCitaPorId(id);

        if (citaExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Cita no encontrada"));
        }

        // Validaciones de actualización
        if (citaActualizada.getFecha() != null) {
            if (citaActualizada.getFecha().isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "La nueva fecha no puede ser pasada"));
            }
            citaExistente.setFecha(citaActualizada.getFecha());
        }

        if (citaActualizada.getHora() != null) {
            // Si la fecha es hoy o se cambió a hoy, validar hora
            LocalDate fechaValidar;

            if (citaActualizada.getFecha() != null) {
                // Si el usuario envió una nueva fecha, usamos esa para validar
                fechaValidar = citaActualizada.getFecha();
            } else {
                // Si no envió fecha (es null), usamos la que ya estaba guardada en la base de datos
                fechaValidar = citaExistente.getFecha();
            }

            if (fechaValidar.equals(LocalDate.now()) && citaActualizada.getHora().isBefore(LocalTime.now())) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "La nueva hora seleccionada ya ha pasado"));
            }
            citaExistente.setHora(citaActualizada.getHora());
        }

        if (citaActualizada.getObservacion() != null) {
            citaExistente.setObservacion(citaActualizada.getObservacion().trim());
        }

        return ResponseEntity.ok(citaRepository.save(citaExistente));
    }

    public ResponseEntity<?> eliminarCita(Long id) {
        Cita citaExistente = buscarCitaPorId(id);
        
        if (citaExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "La cita no existe para ser eliminada"));
        }
        citaRepository.delete(citaExistente);
        return ResponseEntity.ok(Map.of("mensaje", "Cita cancelada exitosamente"));
    }
}
