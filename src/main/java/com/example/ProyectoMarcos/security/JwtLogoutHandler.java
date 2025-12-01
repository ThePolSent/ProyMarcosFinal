package com.example.ProyectoMarcos.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/**
 * Implementación de LogoutHandler para arquitecturas basadas en JWT.
 *
 * CRÍTICO: Esta implementación ahora se encarga de eliminar la cookie JWT
 * del lado del cliente, ya que el servidor no mantiene el estado.
 */
@Component
public class JwtLogoutHandler implements LogoutHandler {

    // Define el nombre de la cookie que contiene el token JWT
    private static final String JWT_TOKEN_NAME = "jwtToken";

    /**
     * Procesa la solicitud de cierre de sesión.
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        // 1. Limpiar el contexto de seguridad de Spring (Lado del Servidor)
        SecurityContextHolder.clearContext();

        // 2. CRÍTICO: Eliminar la cookie JWT del cliente

        // Creamos una nueva cookie con el mismo nombre y valor nulo
        Cookie cookie = new Cookie(JWT_TOKEN_NAME, null);

        // Indicamos que debe expirar inmediatamente (MaxAge=0)
        cookie.setMaxAge(0);

        // Aseguramos que el path sea el mismo que el path original para que el navegador la reemplace
        cookie.setPath("/");

        // IMPORTANTE: Si la cookie original fue HttpOnly (lo cual es altamente recomendado),
        // esta cookie de eliminación también debe serlo.
        cookie.setHttpOnly(true);

        // Si usaste la opción Secure (solo HTTPS), también debes añadirla aquí:
        // cookie.setSecure(true);

        // 3. Añadir la cookie de expiración a la respuesta HTTP
        response.addCookie(cookie);

        // Logging para la consola del servidor
        if (authentication != null) {
            System.out.println("LOGOUT EXITOSO: Usuario " + authentication.getName() + " ha cerrado sesión y se eliminó la cookie.");
        } else {
            System.out.println("LOGOUT EXITOSO. Cookie de JWT eliminada.");
        }

        // Spring Security continuará con la redirección a /login?logout
    }
}