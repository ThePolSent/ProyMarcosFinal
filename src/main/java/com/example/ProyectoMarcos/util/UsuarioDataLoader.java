/*

package com.example.ProyectoMarcos.util;

import com.example.ProyectoMarcos.model.Usuario;
import com.example.ProyectoMarcos.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UsuarioDataLoader implements CommandLineRunner {

    // 1. DECLARACIÓN DE LA VARIABLE (Solo el servicio)
    private final UsuarioService usuarioService;

    // 2. INYECCIÓN A TRAVÉS DEL CONSTRUCTOR (Solo el servicio)
    public UsuarioDataLoader(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("--- INICIANDO CARGA DE DATOS DEL USUARIO ADMINISTRADOR 'ALARCON' (Temporal)... ---");

        // NOTA: Si el usuario ya existe en la base de datos, esto podría causar un error de duplicidad (por el username o correo)
        // si no tienes un control previo (como contarUsuarios() o buscar antes de guardar).

        // --- Usuario ADMINISTRADOR PRINCIPAL ---
        Usuario admin1 = new Usuario();
        admin1.setNombre("Marcelo");
        admin1.setApellido("Alarcon");
        admin1.setUsername("ThePolSent");
        admin1.setCorreo("polsentgr@gmail.com");

        // 🚨 CONTRASENA EN TEXTO PLANO: Se hasheará en el UsuarioService antes de guardarse en la DB.
        admin1.setContrasena("patata");

        admin1.setFechaNacimiento(LocalDate.of(2006, 1, 13));
        admin1.setRol("ADMIN");

        usuarioService.guardarUsuario(admin1);
        System.out.println("ADMIN Creado: " + admin1.getCorreo() + " (Contraseña: patata -> HASHEADA en el servicio)");

        System.out.println("--- Carga de Administrador finalizada. ---");
    }
}
*/
