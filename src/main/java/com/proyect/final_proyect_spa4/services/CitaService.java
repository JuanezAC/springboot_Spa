package com.proyect.final_proyect_spa4.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proyect.final_proyect_spa4.entities.Cita;
import com.proyect.final_proyect_spa4.repositories.CitaRepository;

@Service
public class CitaService {

private final CitaRepository citaRepository;
public CitaService(CitaRepository citaRepository){
    this.citaRepository = citaRepository;
}
public List<Cita> listar(){
    return citaRepository.findAll();
}
public Cita obtenerId(Long id){
    return citaRepository.findById(id).orElse(null);
}
public Cita guardar(Cita cita){
    return citaRepository.save(cita);
}
public Cita actualizar(Long id, Cita citaActualizada){
    Cita citaExistente = obtenerId(id);

    if(citaExistente == null){
        return null;
    }
    citaExistente.setFecha(citaActualizada.getFecha());
    citaExistente.setHora(citaActualizada.getHora());
    citaExistente.setObservacion(citaActualizada.getObservacion());

    return citaRepository.save(citaExistente);
}
public Cita eliminar(Long id){
    Cita citaexistente = obtenerId(id);

    if(citaexistente == null){
        return null;
    }
    citaRepository.delete(citaexistente);
    return citaexistente;
}
}
