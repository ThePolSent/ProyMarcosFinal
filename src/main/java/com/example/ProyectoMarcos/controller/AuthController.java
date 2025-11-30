package com.example.ProyectoMarcos.controller;

import com.example.ProyectoMarcos.model.Usuario;
import com.example.ProyectoMarcos.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

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

        usuarioService.guardarUsuario(usuario);

        redirectAttributes.addFlashAttribute("registroExitoso", "¡Cuenta creada con éxito! Inicia sesión.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam String correo,
            @RequestParam String contrasena,
            RedirectAttributes redirectAttributes,
            HttpSession session)
    {
        Optional<Usuario> optionalUsuario = usuarioService.buscarPorCorreo(correo);

        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();

            // Verificación de Contraseña (NOTA: Usar hasheo en producción)
            if (usuario.getContrasena().equals(contrasena)) {

                session.setAttribute("usuarioLogueado", usuario);
                session.setAttribute("rolUsuario", usuario.getRol());

                System.out.println("Login exitoso para el usuario: " + usuario.getUsername());

                if ("ADMIN".equals(usuario.getRol())) {
                    return "redirect:/admin";
                }

                return "redirect:/";
            }
        }

        redirectAttributes.addFlashAttribute("errorLogin", "Correo o contraseña incorrectos.");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session, RedirectAttributes redirectAttributes) {

        session.invalidate();

        redirectAttributes.addFlashAttribute("logoutExitoso", "Sesión cerrada correctamente. ¡Vuelve pronto!");
        return "redirect:/login";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "profile";
    }

    @GetMapping("/editar-perfil")
    public String mostrarFormularioEditarPerfil(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioDb = usuarioService.buscarPorId(usuarioLogueado.getId());

        if (usuarioDb.isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuarioDb.get());

        return "edit-profile";
    }

    @PostMapping("/editar-perfil")
    public String guardarPerfil(
            @Valid @ModelAttribute("usuario") Usuario usuarioActualizado,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes)
    {
        Usuario usuarioOriginal = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioOriginal == null) {
            return "redirect:/login";
        }

        usuarioActualizado.setId(usuarioOriginal.getId());
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


        Usuario usuarioGuardado = usuarioService.guardarUsuario(usuarioActualizado);

        session.setAttribute("usuarioLogueado", usuarioGuardado);

        redirectAttributes.addFlashAttribute("exito", "¡Tu perfil ha sido actualizado con éxito!");
        return "redirect:/editar-perfil";
    }

    @PostMapping("/editar-perfil/password")
    public String actualizarContrasena(
            @RequestParam("contrasenaActual") String contrasenaActual,
            @RequestParam("nuevaContrasena") String nuevaContrasena,
            @RequestParam("confirmarContrasena") String confirmarContrasena,
            HttpSession session,
            RedirectAttributes redirectAttributes)
    {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        if (!usuarioLogueado.getContrasena().equals(contrasenaActual)) {
            redirectAttributes.addFlashAttribute("errorContrasena", "La contraseña actual es incorrecta.");
            return "redirect:/editar-perfil";
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            redirectAttributes.addFlashAttribute("errorContrasena", "La nueva contraseña y su confirmación no coinciden.");
            return "redirect:/editar-perfil";
        }

        usuarioLogueado.setContrasena(nuevaContrasena);

        Usuario usuarioGuardado = usuarioService.guardarUsuario(usuarioLogueado);

        session.setAttribute("usuarioLogueado", usuarioGuardado);

        redirectAttributes.addFlashAttribute("exitoContrasena", "¡Contraseña actualizada con éxito!");
        return "redirect:/editar-perfil";
    }

    @PostMapping("/perfil/delete")
    public String eliminarPerfil(HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        usuarioService.eliminarUsuario(usuarioLogueado.getId());

        session.invalidate();

        redirectAttributes.addFlashAttribute("logoutExitoso", "¡Tu cuenta ha sido eliminada permanentemente!");
        return "redirect:/login";
    }
}