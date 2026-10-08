package com.proyect.final_proyect_spa4.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyect.final_proyect_spa4.entities.HorarioDisponible;
import com.proyect.final_proyect_spa4.repositories.HorarioRepository;

@Service
public class HorarioExpirationService {

    private static final Logger log = LoggerFactory.getLogger(HorarioExpirationService.class);

    private final HorarioRepository horarioRepository;
    private final HorarioGeneratorService horarioGeneratorService;
    private final SseService sseService;

    public HorarioExpirationService(HorarioRepository horarioRepository,
            HorarioGeneratorService horarioGeneratorService, SseService sseService) {
        this.horarioRepository = horarioRepository;
        this.horarioGeneratorService = horarioGeneratorService;
        this.sseService = sseService;
    }

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void expirarHorariosPasados() {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        // 1) capturar lo que sale del pool ANTES de cambiar el flag
        int perdidos = horarioRepository.findExpiradosEnPool(hoy, ahora).size();

        // 2) marcar vencidos (los que tienen cita quedan como historico con disponible=false)
        horarioRepository.marcarExpirados(hoy, ahora);

        // 3) purgar sin cita: vencidos + profesionales inactivos
        List<HorarioDisponible> purgables = horarioRepository.findPurgables(hoy, ahora);
        horarioRepository.deleteAll(purgables);

        // 4) reposicion 1:1 + tope
        int creados = horarioGeneratorService.generarHorariosAleatorios(perdidos);
        horarioGeneratorService.aplicarLimite();

        if (!purgables.isEmpty() || creados > 0) {
            sseService.enviarEvento("HORARIOS_ACTUALIZADOS");
            log.info("Expirados pool: {}, purgados: {}, creados: {}", perdidos, purgables.size(), creados);
        }
    }
}
