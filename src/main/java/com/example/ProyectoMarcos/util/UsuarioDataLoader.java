/*
package com.example.ProyectoMarcos.util;

import com.example.ProyectoMarcos.model.Usuario;
import com.example.ProyectoMarcos.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UsuarioDataLoader implements CommandLineRunner {

    // 🚨 1. DECLARACIÓN DE LA VARIABLE
    private final UsuarioService usuarioService;

    // 🚨 2. INYECCIÓN A TRAVÉS DEL CONSTRUCTOR
    public UsuarioDataLoader(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("--- FORZANDO CARGA DE 3 USUARIOS ADMINISTRADORES (Temporal)... ---");

        // --- 1. Usuario ADMINISTRADOR PRINCIPAL ---
        Usuario admin1 = new Usuario();
        admin1.setNombre("Marcelo");
        admin1.setApellido("Alarcon");
        admin1.setUsername("PolSent");
        admin1.setCorreo("polsentlgr@gmail.com");
        admin1.setContrasena("patata");
        admin1.setFechaNacimiento(LocalDate.of(2006, 1, 13));
        admin1.setRol("ADMIN");
        usuarioService.guardarUsuario(admin1); // <--- Ahora 'usuarioService' existe
        System.out.println("ADMIN 1 Creado: " + admin1.getCorreo());


        // --- 2. Usuario ADMINISTRADOR SECUNDARIO ---
        Usuario admin2 = new Usuario();
        admin2.setNombre("Junior");
        admin2.setApellido("Zumaeta");
        admin2.setUsername("JuferGo");
        admin2.setCorreo("JuFerGo@gmail.com");
        admin2.setContrasena("bianca");
        admin2.setFechaNacimiento(LocalDate.of(2006, 01, 11));
        admin2.setRol("ADMIN");
        usuarioService.guardarUsuario(admin2);
        System.out.println("ADMIN 2 Creado: " + admin2.getCorreo());


        // --- 3. Usuario ADMINISTRADOR DE PRUEBA ---
        Usuario admin3 = new Usuario();
        admin3.setNombre("Cristian");
        admin3.setApellido("Huaman");
        admin3.setUsername("yasupera");
        admin3.setCorreo("CristianHuaman@gmail.com");
        admin3.setContrasena("superacristian");
        admin3.setFechaNacimiento(LocalDate.of(1988, 11, 30));
        admin3.setRol("ADMIN");
        usuarioService.guardarUsuario(admin3);
        System.out.println("ADMIN 3 Creado: " + admin3.getCorreo());
        System.out.println("--- Carga inicial de 3 Administradores forzada y finalizada. ---");
    }
}

 */