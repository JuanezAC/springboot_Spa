package com.proyect.final_proyect_spa4.services;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseService {

    private static final Logger log = LoggerFactory.getLogger(SseService.class);
    private final Map<String, SseEmitter> clientes = new ConcurrentHashMap<>();

    public SseEmitter registrarCliente() {
        SseEmitter emitter = new SseEmitter(1800000L);
        String id = UUID.randomUUID().toString();
        clientes.put(id, emitter);

        emitter.onCompletion(() -> {
            clientes.remove(id);
            log.info("Cliente SSE desconectado: {}", id);
        });
        emitter.onTimeout(() -> {
            clientes.remove(id);
            log.info("Cliente SSE timeout: {}", id);
        });
        emitter.onError(e -> {
            clientes.remove(id);
            log.warn("Cliente SSE error: {}", id);
        });

        log.info("Cliente SSE conectado: {} (total: {})", id, clientes.size());
        return emitter;
    }

    public void enviarEvento(String tipo) {
        log.info("Enviando evento SSE: {} a {} clientes", tipo, clientes.size());
        clientes.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name(tipo).data("actualizado"));
            } catch (IOException e) {
                log.warn("Error enviando evento SSE a {}: {}", id, e.getMessage());
                clientes.remove(id);
            }
        });
    }
}
