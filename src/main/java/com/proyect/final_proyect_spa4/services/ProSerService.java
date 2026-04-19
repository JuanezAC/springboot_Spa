package com.proyect.final_proyect_spa4.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proyect.final_proyect_spa4.entities.Profesional;
import com.proyect.final_proyect_spa4.entities.ProfesionalServicio;
import com.proyect.final_proyect_spa4.entities.Servicio;
import com.proyect.final_proyect_spa4.repositories.ProSerRepository;
import com.proyect.final_proyect_spa4.repositories.ProfesionalRepository;
import com.proyect.final_proyect_spa4.repositories.ServicioRepository;

@Service
public class ProSerService {
    private final ProSerRepository proSerRepos;
    private final ProfesionalRepository profesionalRep;
    private final ServicioRepository servicioRep;

    public ProSerService(ProSerRepository proSerRepos, ProfesionalRepository profesionalRep, ServicioRepository servicioRep) {
        this.proSerRepos = proSerRepos;
        this.profesionalRep = profesionalRep;
        this.servicioRep = servicioRep;
    }

    // Listar todos
    public ResponseEntity<List<ProfesionalServicio>> buscarTodosProSer(){
        return ResponseEntity.ok(proSerRepos.findAll());
    }

    // Obtener por ID
    public ResponseEntity<?> buscarProSerPorId(Long id){
        ProfesionalServicio proSer = proSerRepos.findById(id).orElse(null);
        
        if(proSer == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Relación profesional-servicio no encontrada"));
        }
        
        return ResponseEntity.ok(proSer);
    }

    // Obtener profesionales de un servicio
    public ResponseEntity<List<ProfesionalServicio>> buscarProSerPorServicio(Long servicioId){
        return ResponseEntity.ok(proSerRepos.findByServicioId(servicioId));
    }

    // Obtener servicios de un profesional
    public ResponseEntity<List<ProfesionalServicio>> buscarProSerPorProfesional(Long profesionalId){
        return ResponseEntity.ok(proSerRepos.findByProfesionalId(profesionalId));
    }

    // Guardar relación profesional-servicio
    public ResponseEntity<?> guardarProSer(ProfesionalServicio proSer){
        // Validar que exista el profesional
        if(proSer.getProfesional() == null || proSer.getProfesional().getId() == null){
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Debe especificar un profesional válido"));
        }

        Profesional profesional = profesionalRep.findById(proSer.getProfesional().getId()).orElse(null);
        if(profesional == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "El profesional especificado no existe"));
        }

        // Validar que exista el servicio
        if(proSer.getServicio() == null || proSer.getServicio().getId() == null){
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Debe especificar un servicio válido"));
        }

        Servicio servicio = servicioRep.findById(proSer.getServicio().getId()).orElse(null);
        if(servicio == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "El servicio especificado no existe"));
        }

        // Validar que no sea un duplicado
        List<ProfesionalServicio> existentes = proSerRepos.findByProfesionalId(profesional.getId());
        boolean yaExiste = existentes.stream()
            .anyMatch(ps -> ps.getServicio().getId().equals(servicio.getId()));
        
        if(yaExiste){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Este profesional ya ofrece este servicio"));
        }

        proSer.setProfesional(profesional);
        proSer.setServicio(servicio);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(proSerRepos.save(proSer));
    }

    // Actualizar relación
    public ResponseEntity<?> actualizarProSer(Long id, ProfesionalServicio proSerActualizado){
        ProfesionalServicio proSerExistente = proSerRepos.findById(id).orElse(null);
        
        if(proSerExistente == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Relación profesional-servicio no encontrada"));
        }

        // Validar profesional si se proporciona
        if(proSerActualizado.getProfesional() != null && proSerActualizado.getProfesional().getId() != null){
            Profesional profesional = profesionalRep.findById(proSerActualizado.getProfesional().getId()).orElse(null);
            if(profesional == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "El profesional especificado no existe"));
            }
            proSerExistente.setProfesional(profesional);
        }

        // Validar servicio si se proporciona
        if(proSerActualizado.getServicio() != null && proSerActualizado.getServicio().getId() != null){
            Servicio servicio = servicioRep.findById(proSerActualizado.getServicio().getId()).orElse(null);
            if(servicio == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "El servicio especificado no existe"));
            }
            proSerExistente.setServicio(servicio);
        }

        return ResponseEntity.ok(proSerRepos.save(proSerExistente));
    }

    // Eliminar relación
    public ResponseEntity<?> eliminarProSer(Long id){
        ProfesionalServicio proSer = proSerRepos.findById(id).orElse(null);
        
        if(proSer == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Relación profesional-servicio no encontrada"));
        }
        
        proSerRepos.delete(proSer);
        return ResponseEntity.ok(Map.of("mensaje", "Relación eliminada correctamente"));
    }
}
