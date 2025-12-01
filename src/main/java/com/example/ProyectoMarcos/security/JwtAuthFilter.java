package com.example.ProyectoMarcos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Inyección de dependencias
    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Intentar extraer el JWT de la cookie "jwtToken"
        final String jwt = extractJwtFromCookie(request);
        final String userEmail;

        // Si no hay token, o el token es nulo, continuar la cadena (el SecurityConfig se encargará de denegar el acceso si la ruta está protegida)
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. Extraer el correo del usuario del token
            userEmail = jwtService.extractUsername(jwt);

            // 3. Si el correo no es nulo y el usuario NO está ya autenticado
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 4. Cargar los detalles del usuario desde la base de datos
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 5. Validar el token y la identidad del usuario
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    // 6. Si es válido, crear un objeto de autenticación
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // Contraseña se establece a null en JWT (autenticación sin estado)
                            userDetails.getAuthorities()
                    );

                    // 7. Establecer detalles de autenticación (IP, sesión, etc.)
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 8. Establecer el usuario como autenticado en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (Exception e) {
            // Manejo de errores de JWT (firma inválida, token expirado, etc.)
            System.err.println("Error al procesar JWT: " + e.getMessage());
            // Opcional: eliminar la cookie inválida para forzar al usuario a loguearse de nuevo
            Cookie invalidCookie = new Cookie("jwtToken", null);
            invalidCookie.setMaxAge(0);
            invalidCookie.setPath("/");
            response.addCookie(invalidCookie);
        }

        // Continuar con los siguientes filtros en la cadena
        filterChain.doFilter(request, response);
    }

    /**
     * Método auxiliar para buscar la cookie 'jwtToken' en la solicitud.
     */
    private String extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "jwtToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}