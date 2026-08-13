package com.proyecto.spa_1.Services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proyecto.spa_1.Entities.Cita;
import com.proyecto.spa_1.Entities.HorarioDisponible;
import com.proyecto.spa_1.Entities.Profesional;
import com.proyecto.spa_1.Entities.Servicio;
import com.proyecto.spa_1.Entities.Usuario;
import com.proyecto.spa_1.Rpositories.CitaRepository;
import com.proyecto.spa_1.Rpositories.HorarioRepository;
import com.proyecto.spa_1.Rpositories.ProfesionalRepository;
import com.proyecto.spa_1.Rpositories.ServicioRepository;
import com.proyecto.spa_1.Rpositories.UsuarioRepository;

@Service
public class CitaService {
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProfesionalRepository profesionalRepository;
    private final ServicioRepository servicioRepository;
    private final HorarioRepository horarioRepository;


    public CitaService(CitaRepository citaRepository, UsuarioRepository usuarioRepository,
            ProfesionalRepository profesionalRepository, ServicioRepository servicioRepository, HorarioRepository horarioRepository) {
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.profesionalRepository = profesionalRepository;
        this.servicioRepository = servicioRepository;
        this.horarioRepository = horarioRepository;
    }

    public List<Cita> obtenerTodos() {
        return citaRepository.findAll();
    }

    public List<Cita> obtenerPorUsuario(Long usuarioId) {
        return citaRepository.findByUsuarioId(usuarioId);
    }

    public Cita obtenerPorId(Long id) {
        return citaRepository.findById(id).orElse(null);
    }

    public ResponseEntity<?> guardar(Cita cita) {
        // Validar que no envíen ID al crear
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
        if (!profesional.getActivo()) {
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

        if (cita.getObservacion() == null || cita.getObservacion().isBlank()) {
            // Determinar origen según el rol del usuario que hace la petición
            String rol = usuario.getRol(); // "ADMIN" o "CLIENTE"
            cita.setObservacion(rol.equals("ADMIN") 
                ? "Reserva realizada por administrador" 
                : "Reserva web");
        }

        // ✅ NUEVO: Validar y asignar el horario
        if (cita.getHorario() == null || cita.getHorario().getId() == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("mensaje", "Debes seleccionar un horario válido"));
        }
        HorarioDisponible horario = horarioRepository.findById(cita.getHorario().getId()).orElse(null);
        if (horario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "El horario seleccionado no existe"));
        }
        if (!horario.getDisponible()) {
            return ResponseEntity.badRequest()
                .body(Map.of("mensaje", "Este horario ya no está disponible"));
        }



        cita.setUsuario(usuario);
        cita.setProfesional(profesional);
        cita.setServicio(servicio);
        cita.setHorario(horario);

        Cita citaGuardada = citaRepository.save(cita);

        horario.setDisponible(false);
        horarioRepository.save(horario); // ← Aquí se actualiza la BD

        return ResponseEntity.status(HttpStatus.CREATED).body(citaGuardada);
    }

    public ResponseEntity<?> actualizar(Long id, Cita citaActualizada) {
        Cita citaExistente = obtenerPorId(id);

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

    public ResponseEntity<?> eliminar(Long id) {
        Cita citaExistente = obtenerPorId(id);
        
        if (citaExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "La cita no existe para ser eliminada"));
        }

        HorarioDisponible horario = citaExistente.getHorario();
        citaRepository.delete(citaExistente);

        if (horario != null) {
            horario.setDisponible(true);
            horarioRepository.save(horario); // Actualizamos el horario para que vuelva a estar disponible
        }
        return ResponseEntity.ok(Map.of("mensaje", "Cita cancelada exitosamente"));
    }
}