package com.proyect.final_proyect_spa4.services;

import java.util.List;

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

    public HorarioDisponible obtenerPorId(Long id){
        return horarioRepository.findById(id).orElse(null);
    }
    
    public HorarioDisponible guardarHorario(HorarioDisponible horario){
        return horarioRepository.save(horario);
    }

    public HorarioDisponible actualizarHorario(Long id, HorarioDisponible horarioActualizado){
        HorarioDisponible horarioExistente = obtenerPorId(id);

        if(horarioExistente == null){
            return null;
        }

        horarioExistente.setFecha(horarioActualizado.getFecha());
        horarioExistente.setHora(horarioActualizado.getHora());
        horarioExistente.setDisponible(horarioActualizado.getDisponible());

        return horarioRepository.save(horarioExistente);
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
