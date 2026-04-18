package com.proyect.final_proyect_spa4.services;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proyect.final_proyect_spa4.entities.ProfesionalServicio;
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

    public ResponseEntity<List<ProfesionalServicio>> obtnerTodos(){
        return ResponseEntity.ok(proSerRepos.findAll());
    }
}
