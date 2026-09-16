package com.proyect.final_proyect_spa4.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyect.final_proyect_spa4.repositories.HorarioRepository;

@Service
public class HorarioExpirationService {

    private static final Logger log = LoggerFactory.getLogger(HorarioExpirationService.class);

    private final HorarioRepository horarioRepository;

    public HorarioExpirationService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void expirarHorariosPasados() {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        int desactivados = horarioRepository.marcarExpirados(hoy, ahora);

        if (desactivados > 0) {
            log.info("Horarios expirados automaticamente: {}", desactivados);
        }
    }
}
