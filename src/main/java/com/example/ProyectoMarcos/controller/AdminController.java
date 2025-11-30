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

    @GetMapping("/mangas")
    public String listMangas(Model model) {
        // CORRECCIÓN: Usar obtenerTodos() en lugar de findAll()
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
        // CORRECCIÓN: Usar buscarPorId() en lugar de findById()
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

        mangaService.guardarManga(manga); // Asumo guardarManga es correcto
        redirectAttributes.addFlashAttribute("successMessage", "Manga guardado con éxito: " + manga.getTitulo());
        return "redirect:/admin/mangas";
    }

    @PostMapping("/mangas/delete/{id}")
    public String deleteManga(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // CORRECCIÓN: Usar eliminarManga() en lugar de deleteById()
        mangaService.eliminarManga(id);
        redirectAttributes.addFlashAttribute("successMessage", "Manga eliminado con éxito.");
        return "redirect:/admin/mangas";
    }

    @GetMapping("/mangakas")
    public String listMangakas(Model model) { // Eliminamos HttpSession ya que no se usa aquí
        // Cambiamos el nombre del atributo a 'listaMangakas' para la plantilla.
        model.addAttribute("listaMangakas", mangakaService.findAll());
        return "mangaka-management";
    }

    @GetMapping("/mangakas/new")
    public String showNewMangakaForm(Model model) {
        model.addAttribute("mangaka", new Mangaka());
        return "mangaka-form";
    }

    @GetMapping("/mangakas/edit/{id}")
    public String showEditMangakaForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        // Se asegura de que el Mangaka exista y lo pasa al modelo.
        Optional<Mangaka> mangakaOpt = mangakaService.findById(id);

        if (mangakaOpt.isPresent()) {
            model.addAttribute("mangaka", mangakaOpt.get());
            return "mangaka-form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Mangaka no encontrado para edición.");
            return "redirect:/admin/mangakas";
        }
    }

    @PostMapping("/mangakas/save")
    public String saveMangaka(@Valid @ModelAttribute("mangaka") Mangaka mangaka,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            // Si hay errores, volvemos al formulario.
            return "mangaka-form";
        }

        mangakaService.guardarMangaka(mangaka);
        redirectAttributes.addFlashAttribute("successMessage", "Mangaka guardado con éxito: " + mangaka.getNombre());
        return "redirect:/admin/mangakas";
    }

    @PostMapping("/mangakas/delete/{id}")
    public String deleteMangaka(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            mangakaService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Mangaka eliminado con éxito.");
        } catch (Exception e) {
            // Manejo de errores si el Mangaka tiene relaciones (ej. mangas) y la DB lo impide.
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo eliminar el Mangaka. Podría tener mangas asociados.");
        }
        return "redirect:/admin/mangakas";
    }


    // =================================================================
    // GESTIÓN DE USUARIOS
    // =================================================================

    @GetMapping("/usuarios")
    public String mostrarGestionUsuarios(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorPermisos", "Acceso denegado. Se requiere rol de Administrador.");
            return "redirect:/";
        }

        List<Usuario> usuarios = usuarioService.obtenerTodos(); // Asumo obtenerTodos() existe
        model.addAttribute("usuarios", usuarios);
        return "user-management";
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

        usuarioService.eliminarUsuario(id); // Asumo eliminarUsuario existe
        redirectAttributes.addFlashAttribute("exitoAdmin", "Usuario con ID " + id + " eliminado correctamente.");

        return "redirect:/admin/usuarios";
    }
}
