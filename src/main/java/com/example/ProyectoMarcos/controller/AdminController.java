package com.example.ProyectoMarcos.controller;

import com.example.ProyectoMarcos.model.Manga;
import com.example.ProyectoMarcos.model.Usuario;
import com.example.ProyectoMarcos.service.MangaService;
import com.example.ProyectoMarcos.service.MangakaService;
import com.example.ProyectoMarcos.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // Protege toda la clase: solo usuarios con rol ADMIN
public class AdminController {

    private final UsuarioService usuarioService;
    private final MangaService mangaService;
    private final MangakaService mangakaService;

    public AdminController(UsuarioService usuarioService, MangaService mangaService, MangakaService mangakaService) {
        this.usuarioService = usuarioService;
        this.mangaService = mangaService;
        this.mangakaService = mangakaService;
    }

    // El método esAdmin ha sido eliminado, ya que la seguridad se maneja con @PreAuthorize

    // =================================================================
    // PANEL PRINCIPAL
    // =================================================================

    @GetMapping
    public String mostrarPanelAdmin(Authentication authentication, Model model) {
        // La validación de rol ya la hizo Spring Security gracias a @PreAuthorize.
        // Usamos Authentication para obtener el correo (que es el nombre de usuario del JWT).
        String correo = authentication.getName();

        usuarioService.buscarPorCorreo(correo).ifPresent(usuario -> {
            model.addAttribute("username", usuario.getUsername());
        });

        return "admin";
    }

    // =================================================================
    // GESTIÓN DE MANGAS
    // =================================================================

    @GetMapping("/mangas")
    public String listMangas(Model model) {
        model.addAttribute("mangas", mangaService.obtenerTodos());
        return "manga-management";
    }

    @GetMapping("/mangas/new")
    public String showNewMangaForm(Model model) {
        model.addAttribute("manga", new Manga());
        model.addAttribute("autores", mangakaService.findAll());
        return "manga-form";
    }

    @GetMapping("/mangas/edit/{id}")
    public String showEditMangaForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return mangaService.buscarPorId(id).map(manga -> {
            model.addAttribute("manga", manga);
            model.addAttribute("autores", mangakaService.findAll());
            return "manga-form";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Manga no encontrado para edición.");
            return "redirect:/admin/mangas";
        });
    }

    @PostMapping("/mangas/save")
    public String saveManga(@Valid @ModelAttribute("manga") Manga manga,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("autores", mangakaService.findAll());
            return "manga-form";
        }

        mangaService.guardarManga(manga);
        redirectAttributes.addFlashAttribute("successMessage", "Manga guardado con éxito: " + manga.getTitulo());
        return "redirect:/admin/mangas";
    }

    @PostMapping("/mangas/delete/{id}")
    public String deleteManga(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        mangaService.eliminarManga(id);
        redirectAttributes.addFlashAttribute("successMessage", "Manga eliminado con éxito.");
        return "redirect:/admin/mangas";
    }

    // =================================================================
    // GESTIÓN DE USUARIOS
    // =================================================================

    @GetMapping("/usuarios")
    public String mostrarGestionUsuarios(Model model) {
        // La validación de rol ya la hizo Spring Security.
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        model.addAttribute("usuarios", usuarios);
        return "user-management";
    }

    @GetMapping("/usuarios/edit/{id}")
    public String showEditUserForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(id);

        if (usuarioOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorAdmin", "Usuario no encontrado para edición.");
            return "redirect:/admin/usuarios";
        }

        Usuario usuario = usuarioOptional.get();
        model.addAttribute("usuario", usuario);
        return "user-form";
    }

    @PostMapping("/usuarios/save")
    public String saveEditedUser(@Valid @ModelAttribute("usuario") Usuario usuario,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "user-form";
        }

        try {
            usuarioService.save(usuario);
            redirectAttributes.addFlashAttribute("exitoAdmin", "Usuario **" + usuario.getUsername() + "** actualizado con éxito.");
            return "redirect:/admin/usuarios";

        } catch (Exception e) {
            String errorMessage = e.getMessage();

            if (errorMessage.contains("username")) {
                model.addAttribute("errorUsername", "Error: " + errorMessage);
            } else if (errorMessage.contains("correo")) {
                model.addAttribute("errorCorreo", "Error: " + errorMessage);
            } else {
                model.addAttribute("errorGeneral", "Error al actualizar el usuario: " + errorMessage);
            }

            return "user-form";
        }
    }

    @PostMapping("/usuarios/delete/{id}")
    public String eliminarUsuarioAdmin(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // Lógica para evitar la auto-eliminación
        String correoAdmin = authentication.getName();
        Optional<Usuario> adminLogueadoOpt = usuarioService.buscarPorCorreo(correoAdmin);

        if (adminLogueadoOpt.isPresent() && adminLogueadoOpt.get().getId().equals(id)) {
            redirectAttributes.addFlashAttribute("errorAdmin", "Un administrador no puede eliminarse a sí mismo desde este panel.");
            return "redirect:/admin/usuarios";
        }

        usuarioService.eliminarUsuario(id);
        redirectAttributes.addFlashAttribute("exitoAdmin", "Usuario con ID " + id + " eliminado correctamente.");

        return "redirect:/admin/usuarios";
    }
}