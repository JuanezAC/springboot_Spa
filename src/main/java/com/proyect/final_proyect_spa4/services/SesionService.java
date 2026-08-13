package com.proyect.final_proyect_spa4.services;

import org.springframework.stereotype.Service;

import com.proyect.final_proyect_spa4.entities.UsuarioSesion;

import jakarta.servlet.http.HttpSession;

@Service
public class SesionService {

    public void guardarUsuario(HttpSession session, UsuarioSesion usuario){
        session.setAttribute("usuario", usuario);
    }

    public UsuarioSesion obtenerUsuario(HttpSession session){
        return (UsuarioSesion) session.getAttribute("usuario");
    }

    public boolean haySesion(HttpSession session){
        return obtenerUsuario(session) != null;
    }

    public boolean esAdmin(HttpSession session){
        UsuarioSesion usuario = obtenerUsuario(session);

        if(usuario == null){
            return false;
        }

        return usuario.getRol().equalsIgnoreCase("ADMIN");
    }

    public Long obtenerUsuarioId(HttpSession session){
        UsuarioSesion usuario = obtenerUsuario(session);
        return usuario == null ? null : usuario.getId();
    }

    public boolean esMismoUsuario(HttpSession session, Long id){
        Long usuarioId = obtenerUsuarioId(session);
        return usuarioId != null && usuarioId.equals(id);
    }

    public void cerrarSesion(HttpSession session){
        session.invalidate();
    }
}
