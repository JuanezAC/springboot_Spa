package com.proyecto.spa_1.Services;


import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyecto.spa_1.Entities.Usuario;
import com.proyecto.spa_1.Rpositories.CitaRepository;
import com.proyecto.spa_1.Rpositories.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CitaRepository citaRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, CitaRepository citaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.citaRepository = citaRepository;
    }

    public Usuario autenticar(String correo, String contrasena){
        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if(usuario != null && passwordEncoder.matches(contrasena, usuario.getContrasena())){
            return usuario;
        }
        return null;
    }

    public List<Usuario> obtenerTodos(){
        return usuarioRepository.findAll();
    }

    public Usuario obtenerPorId(Long id){
        return usuarioRepository.findById(id).orElse(null);
    }

    public ResponseEntity<?> guardar(Usuario usuario) {
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

        if (usuario.getContrasena() == null || usuario.getContrasena().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "La contraseña es obligatoria y no puede estar vacía."));
        }

        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("CLIENTE");
        }

        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);


    }

    public ResponseEntity<?> actualizar(Long id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = obtenerPorId(id);

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
        if (usuarioActualizado.getContrasena() != null && !usuarioActualizado.getContrasena().isBlank()) {
            usuarioExistente.setContrasena(passwordEncoder.encode(usuarioActualizado.getContrasena()));
        }

        return ResponseEntity.ok(usuarioRepository.save(usuarioExistente));
    }


    public ResponseEntity<?> eliminar(Long id) {
        Usuario user = usuarioRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("mensaje", "Usuario no encontrado"));

        long citas = citaRepository.countByUsuarioId(id);
        if (citas > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensaje", "No se puede eliminar: tiene " + citas + " cita(s) registrada(s)."));
        }

        usuarioRepository.delete(user);
        return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado correctamente"));
    }

    

}


