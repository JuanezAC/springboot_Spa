package com.proyect.final_proyect_spa4.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyect.final_proyect_spa4.entities.Usuario;
import com.proyect.final_proyect_spa4.repositories.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario autenticar(String correo, String contrasena){
        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if(usuario != null && passwordEncoder.matches(contrasena, usuario.getContraseña())){
            return usuario;
        }
        return null;
    }

    public List<Usuario> buscarTodosUsuarios(){
        return usuarioRepository.findAll();
    }

    public Usuario buscarUsuarioPorId(Long id){
        return usuarioRepository.findById(id).orElse(null);
    }

    public ResponseEntity<?> guardarUsuario(Usuario usuario) {
        if (usuario.getId() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje", "No se debe enviar el ID al registrar un nuevo usuario"));
        }

        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El nombre es obligatorio y no puede estar vacío"));
        }
        
        if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El correo electrónico es obligatorio y no puede estar vacio."));
        }

        // Limpieza de correo: minúsculas y sin espacios
        String correoLimpio = usuario.getCorreo().trim().toLowerCase();
        usuario.setCorreo(correoLimpio);

        boolean verificarCorreo = usuarioRepository.existsByCorreo(correoLimpio);
        if (verificarCorreo) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensaje", "El correo ya se encuentra registrado"));
        }

        if (usuario.getContraseña() == null || usuario.getContraseña().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "La contraseña es obligatoria y no puede estar vacía."));
        }

        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));

        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    public ResponseEntity<?> actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = buscarUsuarioPorId(id);

        if (usuarioExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "Usuario no encontrado"));
        }

        // Validación estricta de Nombre: Siempre debe venir y no ser vacío
        if (usuarioActualizado.getNombre() == null || usuarioActualizado.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El nombre no puede quedar vacío al actualizar"));
        }
        usuarioExistente.setNombre(usuarioActualizado.getNombre().trim());

        
        
        // Validación de Correo
        if (usuarioActualizado.getCorreo() != null && !usuarioActualizado.getCorreo().isBlank()) {
            String correoLimpio = usuarioActualizado.getCorreo().trim().toLowerCase();
            if (!usuarioExistente.getCorreo().equals(correoLimpio)) {

                boolean verificarCorreo = usuarioRepository.existsByCorreo(correoLimpio);
                if (verificarCorreo) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", "El nuevo correo ya está en uso por otra cuenta"));
                }
                usuarioExistente.setCorreo(correoLimpio);
            }
        }
        
        // Si Rol no es nulo, validamos que no esté en blanco. Si pasa, lo actualizamos.
        if (usuarioActualizado.getRol() != null) {

            if (usuarioActualizado.getRol().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "El rol no puede estar vacío si se intenta modificar"));
            }
            usuarioExistente.setRol(usuarioActualizado.getRol().trim());
        }
        // Si usuarioActualizado.getRol() es null, no entra al bloque y 'usuarioExistente' conserva su rol original.

        // Actualizar contraseña solo si se envía una nueva
        if (usuarioActualizado.getContraseña() != null && !usuarioActualizado.getContraseña().isBlank()) {
            usuarioExistente.setContraseña(passwordEncoder.encode(usuarioActualizado.getContraseña()));
        }

        return ResponseEntity.ok(usuarioRepository.save(usuarioExistente));
    }


    public ResponseEntity<?> eliminarUsuario(Long id) {
        Usuario usuarioExistente = buscarUsuarioPorId(id);

        if (usuarioExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "Usuario no encontrado para eliminar"));
        }

        usuarioRepository.delete(usuarioExistente);
        // return usuarioExistente;
        return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado exitosamente"));
    }
}
