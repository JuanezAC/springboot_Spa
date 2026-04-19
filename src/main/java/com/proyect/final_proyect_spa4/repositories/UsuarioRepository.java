package com.proyect.final_proyect_spa4.repositories;

import java.util.Observable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyect.final_proyect_spa4.entities.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    boolean existsByCorreo(String correo);
    Usuario findByCorreo(String correo);
}
