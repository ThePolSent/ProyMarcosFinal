package com.example.ProyectoMarcos.controller;

import com.example.ProyectoMarcos.model.Mangaka;
import com.example.ProyectoMarcos.model.Manga;
import com.example.ProyectoMarcos.model.Usuario;
import com.example.ProyectoMarcos.service.MangaService;
import com.example.ProyectoMarcos.service.MangakaService;
import com.example.ProyectoMarcos.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;
    private final MangaService mangaService;
    // Se mantiene MangakaService inyectado si es necesario para el CRUD de Manga (ej. listar autores)
    private final MangakaService mangakaService;

    public AdminController(UsuarioService usuarioService, MangaService mangaService, MangakaService mangakaService) {
        this.usuarioService = usuarioService;
        this.mangaService = mangaService;
        this.mangakaService = mangakaService;
    }

    private boolean esAdmin(HttpSession session) {
        String rol = (String) session.getAttribute("rolUsuario");
        return "ADMIN".equals(rol);
    }

    // =================================================================
    // PANEL PRINCIPAL
    // =================================================================

    @GetMapping
    public String mostrarPanelAdmin(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null || !esAdmin(session)) {
            // Redirige al login o a la home con mensaje si no es admin
            redirectAttributes.addFlashAttribute("errorPermisos", "Acceso denegado. Se requiere rol de Administrador.");
            return "redirect:/login";
        }

        model.addAttribute("username", usuario.getUsername());
        return "admin";
    }

    // =================================================================
    // GESTIÓN DE MANGAS (Se mantiene en AdminController)
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
    // GESTIÓN DE MANGAKAS (MOVIDA A MANGAKACONTROLLER)
    // =================================================================
    // -----------------------------------------------------------------


    // =================================================================
    // GESTIÓN DE USUARIOS (Se mantiene en AdminController)
    // =================================================================

    @GetMapping("/usuarios")
    public String mostrarGestionUsuarios(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorPermisos", "Acceso denegado. Se requiere rol de Administrador.");
            return "redirect:/";
        }

        List<Usuario> usuarios = usuarioService.obtenerTodos();
        model.addAttribute("usuarios", usuarios);
        return "user-management";
    }

    // 🚨 NUEVO: Método para mostrar el formulario de edición (GET /admin/usuarios/edit/{id})
    @GetMapping("/usuarios/edit/{id}")
    public String showEditUserForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(id);

        if (usuarioOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorAdmin", "Usuario no encontrado para edición.");
            return "redirect:/admin/usuarios";
        }

        Usuario usuario = usuarioOptional.get();
        // Aseguramos que la contraseña original se mantenga en el modelo
        // para ser pasada como campo oculto en el formulario.

        model.addAttribute("usuario", usuario);
        return "user-form"; // Carga la plantilla templates/user-form.html
    }

    // 🚨 NUEVO: Método para procesar el formulario de edición (POST /admin/usuarios/save)
    @PostMapping("/usuarios/save")
    public String saveEditedUser(@Valid @ModelAttribute("usuario") Usuario usuario,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        // Si hay errores de validación (ej. @Size, @NotBlank), regresa al formulario
        if (result.hasErrors()) {
            // Se puede agregar lógica adicional si es necesario, pero por ahora solo retorna el form
            return "user-form";
        }

        try {
            // Usamos el método save del servicio que maneja la lógica de validación de duplicados (username/correo)
            usuarioService.save(usuario);

            // Éxito: redirigir a la lista de usuarios
            redirectAttributes.addFlashAttribute("exitoAdmin", "Usuario **" + usuario.getUsername() + "** actualizado con éxito.");
            return "redirect:/admin/usuarios";

        } catch (Exception e) {
            // Manejar errores de duplicidad (lanzados desde UsuarioService)
            String errorMessage = e.getMessage();

            // Determinar qué campo causó el error de unicidad
            if (errorMessage.contains("username")) {
                model.addAttribute("errorUsername", "Error: " + errorMessage);
            } else if (errorMessage.contains("correo")) {
                model.addAttribute("errorCorreo", "Error: " + errorMessage);
            } else {
                model.addAttribute("errorGeneral", "Error al actualizar el usuario: " + errorMessage);
            }

            // Volvemos a mostrar el formulario con los datos y el mensaje de error
            return "user-form";
        }
    }

    @PostMapping("/usuarios/delete/{id}")
    public String eliminarUsuarioAdmin(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!esAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorPermisos", "Acceso denegado.");
            return "redirect:/";
        }

        Usuario adminLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (adminLogueado != null && adminLogueado.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("errorAdmin", "Un administrador no puede eliminarse a sí mismo desde este panel.");
            return "redirect:/admin/usuarios";
        }

        usuarioService.eliminarUsuario(id);
        redirectAttributes.addFlashAttribute("exitoAdmin", "Usuario con ID " + id + " eliminado correctamente.");

        return "redirect:/admin/usuarios";
    }
}