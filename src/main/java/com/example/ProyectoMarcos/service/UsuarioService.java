package com.example.ProyectoMarcos.service;

import com.example.ProyectoMarcos.model.Usuario;
import com.example.ProyectoMarcos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    // 1. INYECTAR EL REPOSITORIO JPA
    @Autowired
    private UsuarioRepository usuarioRepository;

    // 2. Método para guardar o registrar un nuevo usuario (C de CRUD)
    public Usuario guardarUsuario(Usuario usuario) {
        // En un proyecto real, aquí deberías hashear la contraseña
        // antes de guardar (usando BCryptPasswordEncoder).
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
        // Asumiendo que el ID en tu entidad Usuario es de tipo Long
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

    // 🚨 NUEVO MÉTODO PARA GUARDAR/ACTUALIZAR USUARIO (USADO EN EDICIÓN ADMIN Y PERFIL)
    public Usuario save(Usuario usuario) throws Exception {
        // En este punto, 'usuario' ya trae su ID si es una edición (o null si es creación).

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

        // La entidad Usuario pasa las validaciones de unicidad y se procede a guardar.
        // Si el ID existe, JPA lo actualiza. Si no existe, lo crea.
        return usuarioRepository.save(usuario);
    }
}