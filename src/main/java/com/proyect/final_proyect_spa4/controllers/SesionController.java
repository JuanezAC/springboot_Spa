package com.proyect.final_proyect_spa4.controllers;

public class SesionController {

}


package com.proyecto.spa_1.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.spa_1.Entities.Usuario;
import com.proyecto.spa_1.Entities.UsuarioSesion;
import com.proyecto.spa_1.Services.SesionService;
import com.proyecto.spa_1.Services.UsuarioService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("api/sesion")
public class SesionController {
    private final SesionService sesionService;
    private final UsuarioService usuarioService;

    public SesionController(SesionService sesionService, UsuarioService usuarioService) {
        this.sesionService = sesionService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioSesion datos, HttpSession session){
        Usuario usuario = usuarioService.autenticar(datos.getCorreo(), datos.getContrasena());

        if(usuario == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Credenciales innvalidas"));
        }

        UsuarioSesion usuarioSesion = new UsuarioSesion();
        usuarioSesion.setId(usuario.getId());
        usuarioSesion.setNombre(usuario.getNombre());
        usuarioSesion.setCorreo(usuario.getCorreo());
        usuarioSesion.setRol(usuario.getRol());
        usuarioSesion.setContrasena(null); // No incluir la contraseña en la sesión

        sesionService.guardarUsuario(session, usuarioSesion);

        return ResponseEntity.ok(Map.of(
            "mensaje", "Inicio de sesión exitoso",
            "usuario", usuarioSesion
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session){
        sesionService.cerrarSesion(session);

        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada exitosamente"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session){
        UsuarioSesion usuario = sesionService.obtenerUsuario(session);

        if(usuario == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "No hay una sesión activa"));
        }

        return ResponseEntity.ok(usuario);
    }

    
}

