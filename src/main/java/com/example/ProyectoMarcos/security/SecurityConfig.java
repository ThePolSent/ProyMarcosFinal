package com.example.ProyectoMarcos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler; // Importación necesaria

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final LogoutHandler jwtLogoutHandler; // Inyectamos el handler de logout

    // Inyectamos el JwtLogoutHandler en el constructor
    public SecurityConfig(LogoutHandler jwtLogoutHandler) {
        this.jwtLogoutHandler = jwtLogoutHandler;
    }

    /**
     * Configuración del filtro de seguridad HTTP.
     * Inyectamos JwtAuthFilter y AuthenticationProvider como parámetros.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter,
            AuthenticationProvider authenticationProvider
    ) throws Exception {
        http
                // 1. Deshabilitar CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Definir las reglas de autorización de las rutas
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas y de autenticación (Registro/Login)
                        .requestMatchers(
                                "/", "/index", "/login", "/registro", "/registrar",
                                "/css/**", "/js/**", "/images/**", "/img/**", "/data/**"
                        ).permitAll()

                        // 3. Rutas de administración (Solo rol ADMIN)
                        .requestMatchers("/admin", "/admin/**", "/user-management/**", "/manga-management/**", "/mangaka-management/**").hasAuthority("ROLE_ADMIN")

                        // 4. Otras rutas (requieren cualquier usuario autenticado)
                        .anyRequest().authenticated()
                )

                // 5. Configuración de Sesión (sin estado para JWT)
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 6. Configurar el proveedor de autenticación
                .authenticationProvider(authenticationProvider)

                // 7. Añadir el filtro JWT antes del filtro principal de Spring Security
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // 8. Configuración del Logout para JWT
                .logout(logout -> logout
                                // La URL a la que el formulario de logout debe apuntar
                                .logoutUrl("/logout")
                                // Aplicar el handler personalizado para JWT
                                .addLogoutHandler(jwtLogoutHandler)
                                // CRÍTICO: Redirige al usuario a la página de login después de un logout exitoso.
                                .logoutSuccessUrl("/login?logout")

                        // ELIMINAMOS el antiguo .logoutSuccessHandler que solo devolvía 200:
                        // .logoutSuccessHandler((request, response, authentication) -> {
                        //     response.setStatus(200);
                        // })
                );

        return http.build();
    }

    /**
     * Define el AuthenticationProvider que usará nuestro UserDetailsService y PasswordEncoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Define el PasswordEncoder para usar BCrypt, el estándar de la industria.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean necesario para el proceso de autenticación de Spring Security.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}