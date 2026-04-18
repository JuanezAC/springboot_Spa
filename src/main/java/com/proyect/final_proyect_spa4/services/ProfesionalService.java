package com.proyect.final_proyect_spa4.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proyect.final_proyect_spa4.entities.HorarioDisponible;
import com.proyect.final_proyect_spa4.entities.Profesional;
import com.proyect.final_proyect_spa4.repositories.HorarioRepository;
import com.proyect.final_proyect_spa4.repositories.ProfesionalRepository;

@Service
public class ProfesionalService {

private final ProfesionalRepository profesionalRepository;

public ProfesionalService(ProfesionalRepository profesionalRepository, HorarioRepository horarioRepository){
    this.profesionalRepository = profesionalRepository;
}

//Obtener todos - Listar
public List<Profesional> obtener(){
    return profesionalRepository.findAll();
}

//Obtener por id
public Profesional obtenerId(Long id){
    return profesionalRepository.findById(id).orElse(null);
}

//Guardar
public Profesional guardar(Profesional profesional){
    if(profesional.getHorariosDisponibles() != null){
        for(HorarioDisponible horarioDisponible : profesional.getHorariosDisponibles()){
            horarioDisponible.setProfesional(profesional);
        }
    }
return profesionalRepository.save(profesional);
}

//Actualizar
public Profesional actualizar(Long id, Profesional profesionalActualizado){
    Profesional profesionalExistente = obtenerId(id);

    if(profesionalExistente == null){
        return null;
    }
    
    profesionalExistente.setNombre(profesionalActualizado.getNombre());
    profesionalExistente.setEspecialidad(profesionalActualizado.getEspecialidad());
    profesionalExistente.setEmail(profesionalActualizado.getEmail());
    profesionalExistente.setTelefono(profesionalActualizado.getTelefono());
    profesionalExistente.setEstado(profesionalActualizado.getEstado());

    if(profesionalActualizado.getHorariosDisponibles()!= null){
        List <HorarioDisponible> nuevosHorarios = new ArrayList<>();

        for(HorarioDisponible horarioDisponible : profesionalActualizado.getHorariosDisponibles()){
            HorarioDisponible nuevoHorario = new HorarioDisponible();
            nuevoHorario.setFecha(horarioDisponible.getFecha());
            nuevoHorario.setHora(horarioDisponible.getHora());
            nuevoHorario.setDisponible(horarioDisponible.getDisponible());
            nuevoHorario.setFecha(horarioDisponible.getFecha());

            nuevosHorarios.add(nuevoHorario);
        }
        profesionalExistente.getHorariosDisponibles().addAll(nuevosHorarios);
    }
    return profesionalRepository.save(profesionalExistente);
}

//Eliminar
public Profesional eliminar(Long id){
    Profesional profesionalExistente = obtenerId(id);

    if(profesionalExistente == null){
        return null;
    }
    profesionalRepository.delete(profesionalExistente);
    return profesionalExistente;
}


}
