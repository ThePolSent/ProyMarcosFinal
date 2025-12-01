package com.example.ProyectoMarcos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para manejar la solicitud de cierre de sesión.
 *
 * En una arquitectura JWT con Spring Security, este endpoint es la URL a la que
 * apunta el formulario de logout de Thymeleaf (/logout). La lógica real de
 * limpieza del contexto de seguridad la gestiona el JwtLogoutHandler configurado
 * en SecurityConfig.
 *
 * El cliente (navegador/JS) es responsable de eliminar el token JWT
 * de su almacenamiento local o cookies.
 */
@RestController // Usamos @RestController si el logout se maneja por petición AJAX/POST pura
@RequestMapping("/api/auth")
public class LogoutController {

    // Este endpoint es redundante si usas el formulario POST a /logout manejado por
    // SecurityConfig. La configuración de Spring Security ya intercepta /logout.
    // Lo incluimos si la ruta en SecurityConfig fuera /api/auth/logout.

    // Si tu formulario HTML usa: <form th:action="@{/logout}" method="post">
    // Spring Security lo maneja directamente y este controlador NO se necesita.
    // Sin embargo, si lo usas como endpoint POST RESTful, debe quedar así:
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Esta respuesta se enviará si se accede a POST /api/auth/logout.
        // Si el logout es manejado por el formulario de Thymeleaf a /logout (como en tu SecurityConfig),
        // esta respuesta NO se mostrará, ya que el LogoutSuccessHandler de SecurityConfig
        // toma el control y devuelve 200 o redirige.

        return ResponseEntity.ok("Logout exitoso. El servidor limpió el contexto. Por favor, asegúrate de eliminar el token JWT del cliente.");
    }
}