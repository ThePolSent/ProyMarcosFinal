package com.example.ProyectoMarcos.controller;

import com.example.ProyectoMarcos.model.Usuario;
import com.example.ProyectoMarcos.service.UsuarioService;
import com.example.ProyectoMarcos.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder; // << NUEVA IMPORTACIÓN CRÍTICA

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder; // << CRÍTICO: Variable para el PasswordEncoder

    public AuthController(
            UsuarioService usuarioService,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder) // << CRÍTICO: Añadir al constructor
    {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder; // << Asignación
    }

    // --- Métodos de Registro ---

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Usuario());
        }
        return "register";
    }

    @PostMapping("/registrar")
    public String registrarUsuario(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            @RequestParam("confirmarContrasena") String confirmarContrasena,
            RedirectAttributes redirectAttributes)
    {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.usuario", result);
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/registro";
        }

        if (!usuario.getContrasena().equals(confirmarContrasena)) {
            redirectAttributes.addFlashAttribute("errorContrasena", "Las contraseñas no coinciden.");
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/registro";
        }

        if (usuarioService.buscarPorCorreo(usuario.getCorreo()).isPresent()) {
            redirectAttributes.addFlashAttribute("errorCorreo", "El correo ya está registrado.");
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/registro";
        }

        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("USER");
        }

        // El servicio de usuario (que ahora debe estar configurado con BCrypt) se encarga
        // de hashear la contraseña antes de guardarla en la DB.
        usuarioService.guardarUsuario(usuario);

        redirectAttributes.addFlashAttribute("registroExitoso", "¡Cuenta creada con éxito! Inicia sesión.");
        return "redirect:/login";
    }

    // --- Métodos de Login y Logout ---

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam String correo,
            @RequestParam String contrasena,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes)
    {
        try {
            logger.info("Intento de autenticación para correo: {}", correo);

            // 1. INTENTAR AUTENTICAR USANDO EL MANAGER
            // El AuthenticationManager, configurado con BCryptPasswordEncoder, ahora puede
            // comparar la 'contrasena' de texto plano con el hash almacenado en la DB.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(correo, contrasena)
            );

            logger.info("Autenticación exitosa para el correo: {}", correo);

            String principal = authentication.getName();

            Optional<Usuario> optionalUsuario = usuarioService.buscarPorCorreo(principal);
            if (optionalUsuario.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorLogin", "Error interno al cargar el usuario.");
                return "redirect:/login";
            }
            Usuario usuario = optionalUsuario.get();

            // 2. GENERAR JWT
            String token = jwtService.generateToken(usuario.getCorreo(), usuario.getRol());

            // 3. CREAR COOKIE HTTP-ONLY PARA EL TOKEN
            Cookie cookie = new Cookie("jwtToken", token);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 días
            cookie.setPath("/");
            response.addCookie(cookie);

            // 4. Redirigir según el rol
            if ("ADMIN".equals(usuario.getRol())) {
                return "redirect:/admin";
            }

            return "redirect:/";

        } catch (AuthenticationException e) {
            logger.error("Fallo de autenticación para el correo {}: {}", correo, e.getMessage());

            // Maneja cualquier fallo de autenticación
            redirectAttributes.addFlashAttribute("errorLogin", "Correo o contraseña incorrectos.");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpServletResponse response, RedirectAttributes redirectAttributes) {

        // ELIMINAR LA COOKIE JWT
        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        redirectAttributes.addFlashAttribute("logoutExitoso", "Sesión cerrada correctamente. ¡Vuelve pronto!");
        return "redirect:/login";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Authentication authentication, Model model) {

        String correo = authentication.getName();
        Optional<Usuario> optionalUsuario = usuarioService.buscarPorCorreo(correo);

        if (optionalUsuario.isEmpty()) {
            return "redirect:/logout";
        }

        model.addAttribute("usuario", optionalUsuario.get());
        return "profile";
    }

    @GetMapping("/editar-perfil")
    public String mostrarFormularioEditarPerfil(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {

        String correo = authentication.getName();
        Optional<Usuario> optionalUsuario = usuarioService.buscarPorCorreo(correo);

        if (optionalUsuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorGeneral", "Error al recuperar datos del perfil.");
            return "redirect:/logout";
        }

        model.addAttribute("usuario", optionalUsuario.get());
        return "edit-profile";
    }

    @PostMapping("/editar-perfil")
    public String guardarPerfil(
            @Valid @ModelAttribute("usuario") Usuario usuarioActualizado,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes)
    {

        String correoOriginal = authentication.getName();
        Optional<Usuario> optionalUsuarioOriginal = usuarioService.buscarPorCorreo(correoOriginal);

        if (optionalUsuarioOriginal.isEmpty()) {
            return "redirect:/logout";
        }

        Usuario usuarioOriginal = optionalUsuarioOriginal.get();

        usuarioActualizado.setId(usuarioOriginal.getId());
        // CRÍTICO: Mantener el HASH de la contraseña original, no el texto plano.
        usuarioActualizado.setContrasena(usuarioOriginal.getContrasena());

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.usuario", result);
            redirectAttributes.addFlashAttribute("usuario", usuarioActualizado);
            redirectAttributes.addFlashAttribute("errorGeneral", "Corrige los errores en el formulario.");
            return "redirect:/editar-perfil";
        }

        Optional<Usuario> usuarioPorCorreo = usuarioService.buscarPorCorreo(usuarioActualizado.getCorreo());
        if (usuarioPorCorreo.isPresent() && !usuarioPorCorreo.get().getId().equals(usuarioActualizado.getId())) {
            redirectAttributes.addFlashAttribute("errorCorreo", "El correo ya está asociado a otra cuenta.");
            redirectAttributes.addFlashAttribute("usuario", usuarioActualizado);
            return "redirect:/editar-perfil";
        }

        Optional<Usuario> usuarioPorUsername = usuarioService.buscarPorUsername(usuarioActualizado.getUsername());
        if (usuarioPorUsername.isPresent() && !usuarioPorUsername.get().getId().equals(usuarioActualizado.getId())) {
            redirectAttributes.addFlashAttribute("errorUsername", "El username ya está en uso.");
            redirectAttributes.addFlashAttribute("usuario", usuarioActualizado);
            return "redirect:/editar-perfil";
        }

        usuarioActualizado.setRol(usuarioOriginal.getRol());

        try {
            // Usamos 'save' del servicio que realiza las validaciones y guarda el usuario con el hash existente.
            usuarioService.save(usuarioActualizado);
        } catch (Exception e) {
            logger.error("Error al guardar perfil: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorGeneral", "Error al actualizar el perfil.");
            return "redirect:/editar-perfil";
        }

        redirectAttributes.addFlashAttribute("exito", "¡Tu perfil ha sido actualizado con éxito!");
        return "redirect:/editar-perfil";
    }

    @PostMapping("/editar-perfil/password")
    public String actualizarContrasena(
            @RequestParam("contrasenaActual") String contrasenaActual,
            @RequestParam("nuevaContrasena") String nuevaContrasena,
            @RequestParam("confirmarContrasena") String confirmarContrasena,
            Authentication authentication,
            RedirectAttributes redirectAttributes)
    {
        String correo = authentication.getName();
        Optional<Usuario> optionalUsuarioLogueado = usuarioService.buscarPorCorreo(correo);

        if (optionalUsuarioLogueado.isEmpty()) {
            return "redirect:/logout";
        }

        Usuario usuarioLogueado = optionalUsuarioLogueado.get();

        // ⚠️ CRÍTICO: USAR BCryptPasswordEncoder.matches para comparar texto plano vs hash.
        if (!passwordEncoder.matches(contrasenaActual, usuarioLogueado.getContrasena())) {
            redirectAttributes.addFlashAttribute("errorContrasena", "La contraseña actual es incorrecta.");
            return "redirect:/editar-perfil";
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            redirectAttributes.addFlashAttribute("errorContrasena", "La nueva contraseña y su confirmación no coinciden.");
            return "redirect:/editar-perfil";
        }

        // Se usa el servicio que hashea la nueva contraseña y guarda el usuario.
        usuarioService.guardarUsuarioConContrasenaNueva(usuarioLogueado, nuevaContrasena);

        redirectAttributes.addFlashAttribute("exitoContrasena", "¡Contraseña actualizada con éxito!");
        return "redirect:/editar-perfil";
    }

    @PostMapping("/perfil/delete")
    public String eliminarPerfil(Authentication authentication, HttpServletResponse response, RedirectAttributes redirectAttributes) {

        String correo = authentication.getName();
        Optional<Usuario> optionalUsuarioLogueado = usuarioService.buscarPorCorreo(correo);

        if (optionalUsuarioLogueado.isEmpty()) {
            return "redirect:/login";
        }

        Usuario usuarioLogueado = optionalUsuarioLogueado.get();
        usuarioService.eliminarUsuario(usuarioLogueado.getId());

        // ELIMINAR LA COOKIE JWT
        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        redirectAttributes.addFlashAttribute("logoutExitoso", "¡Tu cuenta ha sido eliminada permanentemente!");
        return "redirect:/login";
    }
}