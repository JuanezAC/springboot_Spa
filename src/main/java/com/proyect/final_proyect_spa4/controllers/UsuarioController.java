package com.proyect.final_proyect_spa4.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyect.final_proyect_spa4.entities.Usuario;
import com.proyect.final_proyect_spa4.entities.UsuarioSesion;
import com.proyect.final_proyect_spa4.services.SesionService;
import com.proyect.final_proyect_spa4.services.UsuarioService;

import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("api/usuarios")
public class UsuarioController {
    public final UsuarioService usuarioService;
    public final SesionService sesionService;

    public UsuarioController(UsuarioService usuarioService, SesionService sesionService) {
        this.usuarioService = usuarioService;
        this.sesionService = sesionService;
    }

    @GetMapping
    public ResponseEntity<?> buscarTodosUsuarios(HttpSession session){
        if (!sesionService.haySesion(session)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debes iniciar sesion"));
        }

        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", "No tienes permisos para ver todos los usuarios"));
        }

        return ResponseEntity.ok(usuarioService.buscarTodosUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarUsuarioPorId(@PathVariable Long id, HttpSession session){
        if (!sesionService.haySesion(session)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debes iniciar sesión"));
        }

        if (!sesionService.esAdmin(session) && !sesionService.esMismoUsuario(session, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", "No tienes permisos para ver este usuario"));
        }

        Usuario usuario = usuarioService.buscarUsuarioPorId(id);

        if(usuario == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "usuario no encontrado"));
        }
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarCliente(@RequestBody Usuario usuario) {
        try {
            return usuarioService.guardarUsuario(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error interno al procesar el registro", "error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> guardarUsuario(@RequestBody Usuario usuario, HttpSession session) {
        if (!sesionService.haySesion(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debes iniciar sesión"));
        }
        
        if (!sesionService.esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", "No tienes permisos para guardar usuarios"));
        }

        try {
            return usuarioService.guardarUsuario(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "mensaje", "Ocurrió un error inesperado al guardar el usuario",
                    "error", e.getMessage()
                ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario, HttpSession session){
        if (!sesionService.haySesion(session)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debes iniciar sesión"));
        }

        if (!sesionService.esAdmin(session) && !sesionService.esMismoUsuario(session, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", "No tienes permisos para actualizar este usuario"));
        }

        try {
            return usuarioService.actualizarUsuario(id, usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error interno al actualizar", "error", e.getMessage()));
        }
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id, HttpSession session){
        if (!sesionService.haySesion(session)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Debes iniciar sesión"));
        }
        if(!sesionService.esAdmin(session)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", "No tienes permisos para eliminar usuarios"));
        }

        try {
            return usuarioService.eliminarUsuario(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error interno al eliminar", "error", e.getMessage()));
        }
    }
}