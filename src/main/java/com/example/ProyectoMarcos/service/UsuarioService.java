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

    // 🚨 MÉTODO FALTANTE (SOLUCIÓN): 7. Método para ELIMINAR un usuario por ID
    public void eliminarUsuario(Long id) {
        // Llama al método estándar de JpaRepository para la eliminación por ID
        usuarioRepository.deleteById(id);
    }
}