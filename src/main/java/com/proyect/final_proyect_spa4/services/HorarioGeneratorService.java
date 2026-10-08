package com.proyect.final_proyect_spa4.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyect.final_proyect_spa4.entities.HorarioDisponible;
import com.proyect.final_proyect_spa4.entities.Profesional;
import com.proyect.final_proyect_spa4.repositories.CitaRepository;
import com.proyect.final_proyect_spa4.repositories.HorarioRepository;
import com.proyect.final_proyect_spa4.repositories.ProfesionalRepository;

@Service
public class HorarioGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(HorarioGeneratorService.class);

    private static final int MAX_INTENTOS = 50;

    private final HorarioRepository horarioRepository;
    private final ProfesionalRepository profesionalRepository;
    private final CitaRepository citaRepository;

    @Value("${horarios.limite:50}")
    private int limite;

    @Value("${horarios.dias-futuros:30}")
    private int diasFuturos;

    @Value("${horarios.hora-inicio:08:00}")
    private LocalTime horaInicio;

    @Value("${horarios.hora-fin:19:00}")
    private LocalTime horaFin;

    @Value("${horarios.paso-minutos:30}")
    private int pasoMinutos;

    public HorarioGeneratorService(HorarioRepository horarioRepository,
            ProfesionalRepository profesionalRepository, CitaRepository citaRepository) {
        this.horarioRepository = horarioRepository;
        this.profesionalRepository = profesionalRepository;
        this.citaRepository = citaRepository;
    }

    public HorarioDisponible generarHorarioAleatorio() {
        List<Profesional> activos = profesionalRepository.findByEstadoTrue();
        if (activos.isEmpty()) {
            log.warn("No hay profesionales activos para generar horarios");
            return null;
        }

        long pasosTotales = pasoMinutos <= 0 ? 0
                : horaInicio.until(horaFin, ChronoUnit.MINUTES) / pasoMinutos;
        if (pasosTotales < 0) {
            log.warn("Rango de horas invalido: inicio {} es despues de fin {}", horaInicio, horaFin);
            return null;
        }

        int rangoDias = Math.max(diasFuturos, 1);

        for (int intento = 0; intento < MAX_INTENTOS; intento++) {
            Profesional profesional = activos.get(ThreadLocalRandom.current().nextInt(activos.size()));
            LocalDate fecha = LocalDate.now()
                    .plusDays(1 + ThreadLocalRandom.current().nextInt(rangoDias));
            long k = ThreadLocalRandom.current().nextLong(pasosTotales + 1);
            LocalTime hora = horaInicio.plusMinutes(k * pasoMinutos);

            if (horarioRepository.existsByProfesionalIdAndFechaAndHora(profesional.getId(), fecha, hora)) {
                continue;
            }
            if (citaRepository.existsByProfesionalIdAndFechaAndHora(profesional.getId(), fecha, hora)) {
                continue;
            }

            HorarioDisponible horario = new HorarioDisponible();
            horario.setFecha(fecha);
            horario.setHora(hora);
            horario.setDisponible(true);
            horario.setProfesional(profesional);
            return horarioRepository.save(horario);
        }

        log.warn("No se pudo generar un horario aleatorio tras {} intentos", MAX_INTENTOS);
        return null;
    }

    public int generarHorariosAleatorios(int cantidad) {
        int creados = 0;
        for (int i = 0; i < cantidad; i++) {
            if (generarHorarioAleatorio() == null) {
                break;
            }
            creados++;
        }
        return creados;
    }

    @Transactional
    public void aplicarLimite() {
        aplicarLimiteInterno(null);
    }

    @Transactional
    public void aplicarLimite(Long excluirId) {
        aplicarLimiteInterno(excluirId);
    }

    private void aplicarLimiteInterno(Long excluirId) {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        List<HorarioDisponible> pool = horarioRepository.findPoolOrdenadoPorVencimiento(hoy, ahora);
        long sobrantes = pool.size() - limite;

        for (HorarioDisponible horario : pool) {
            if (sobrantes <= 0) {
                break;
            }
            if (excluirId != null && excluirId.equals(horario.getId())) {
                continue;
            }
            horarioRepository.delete(horario);
            sobrantes--;
        }

        if (sobrantes > 0) {
            log.warn("Pool no reducible a {}: sobran {}", limite, sobrantes);
        }
    }
}
