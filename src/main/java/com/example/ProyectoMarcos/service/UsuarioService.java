package com.example.ProyectoMarcos.service;

import com.example.ProyectoMarcos.model.Usuario;
import com.example.ProyectoMarcos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // << NUEVO
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // << CRÍTICO: Inyección del Encoder

    /**
     * Guarda o registra un nuevo usuario. Hashea la contraseña si es texto plano.
     */
    public Usuario guardarUsuario(Usuario usuario) {
        String contrasena = usuario.getContrasena();

        // ⚠️ CRÍTICO: SOLO HASHEAR si la contraseña no tiene ya formato de hash (ej: si empieza con $2a$).
        // Al registrar, siempre es texto plano, así que hasheamos.
        // También verifica si el campo no está ya hasheado.
        if (contrasena != null && !contrasena.startsWith("$2a$") && !contrasena.startsWith("$2b$") && !contrasena.startsWith("$2y$")) {
            String hash = passwordEncoder.encode(contrasena);
            usuario.setContrasena(hash);
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Método especializado para actualizar solo la contraseña desde texto plano.
     */
    public Usuario guardarUsuarioConContrasenaNueva(Usuario usuario, String nuevaContrasenaTextoPlano) {
        String passwordHash = passwordEncoder.encode(nuevaContrasenaTextoPlano);
        usuario.setContrasena(passwordHash);
        return usuarioRepository.save(usuario);
    }

    // 3. Método para buscar por Correo (usado en el login o validación)
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    // 4. Método para buscar por Username (clave para la autenticación)
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    // 5. Método para buscar por ID (necesario para la edición)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // 6. Método para obtener todos (L de CRUD)
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    // 7. Método para ELIMINAR un usuario por ID
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    // 🚨 MÉTODO PARA GUARDAR/ACTUALIZAR (mantiene el hash existente si es una actualización de perfil sin cambio de pass)
    public Usuario save(Usuario usuario) throws Exception {

        // 1. Verificar duplicidad de username (solo si no es el usuario actual)
        Optional<Usuario> existingUsername = usuarioRepository.findByUsername(usuario.getUsername());
        if (existingUsername.isPresent() && !existingUsername.get().getId().equals(usuario.getId())) {
            throw new Exception("El username '" + usuario.getUsername() + "' ya está en uso por otra cuenta.");
        }

        // 2. Verificar duplicidad de correo (solo si no es el usuario actual)
        Optional<Usuario> existingCorreo = usuarioRepository.findByCorreo(usuario.getCorreo());
        if (existingCorreo.isPresent() && !existingCorreo.get().getId().equals(usuario.getId())) {
            throw new Exception("El correo '" + usuario.getCorreo() + "' ya está asociado a otra cuenta.");
        }

        // El usuario ya trae la contraseña hasheada original (porque se asignó en AuthController)
        return usuarioRepository.save(usuario);
    }
}