package com.proyecto.spa_1.Rpositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.spa_1.Entities.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    Usuario findByCorreo(String correo);
    boolean existsByCorreo(String correo);

}

