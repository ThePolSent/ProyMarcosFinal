package com.example.ProyectoMarcos.security;

import com.example.ProyectoMarcos.model.Usuario;
import com.example.ProyectoMarcos.service.UsuarioService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    private final UsuarioService usuarioService;

    public UsuarioDetailsServiceImpl(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Carga el usuario desde la base de datos basado en el correo (el "username" en el contexto de Spring Security).
     * @param correo El correo del usuario (que es el Subject/Username del JWT).
     * @return Un objeto UserDetails de Spring Security.
     * @throws UsernameNotFoundException si el usuario no existe.
     */
    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {

        // 1. Buscar el usuario en la base de datos
        Usuario usuario = usuarioService.buscarPorCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));

        // 2. Mapear el rol a una lista de GrantedAuthority
        // Se añade el prefijo "ROLE_" necesario para el control de acceso en Spring Security
        String rolConPrefijo = "ROLE_" + usuario.getRol().toUpperCase();
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(rolConPrefijo)
        );

        // 3. Devolver el objeto UserDetails
        // CRÍTICO: Devolvemos la contraseña tal cual. Asumimos que esta contraseña es el hash BCrypt
        // almacenado en la base de datos por el UsuarioService.
        return new org.springframework.security.core.userdetails.User(
                usuario.getCorreo(),        // El nombre de usuario (correo)
                usuario.getContrasena(),    // La contraseña (el HASH de la DB)
                authorities                 // La lista de roles/autoridades
        );
    }
}