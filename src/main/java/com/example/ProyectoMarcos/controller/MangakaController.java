package com.example.ProyectoMarcos.controller;

import com.example.ProyectoMarcos.model.Mangaka;
import com.example.ProyectoMarcos.model.Manga;
import com.example.ProyectoMarcos.service.MangakaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@Controller
public class MangakaController {

    private final MangakaService mangakaService;

    public MangakaController(MangakaService mangakaService) {
        this.mangakaService = mangakaService;
    }

    // ======================================================================
    // 1. RUTAS PÚBLICAS (CATÁLOGO)
    // ======================================================================

    @GetMapping("/mangakas")
    public String getCatalogoMangakas(Model model) {
        model.addAttribute("mangakas", mangakaService.findAll());
        return "mangakas";
    }

    @GetMapping("/mangakas/{id}")
    public String getMangakaDetails(@PathVariable Long id, Model model) {
        Optional<Mangaka> mangakaOpt = mangakaService.findById(id);

        if (mangakaOpt.isPresent()) {
            Mangaka mangaka = mangakaOpt.get();
            List<Manga> mangasAutor = mangakaService.findMangaByAuthorId(id);

            model.addAttribute("mangaka", mangaka);
            model.addAttribute("mangasAutor", mangasAutor);

            return "mangaka-details";
        } else {
            return "error/404";
        }
    }

    // ======================================================================
    // 2. RUTAS DE GESTIÓN (ADMIN)
    // ======================================================================

    @GetMapping("/admin/mangakas")
    public String listMangakas(Model model) {
        List<Mangaka> listaMangakas = mangakaService.findAll();
        model.addAttribute("listaMangakas", listaMangakas);
        return "mangaka-management";
    }

    /**
     * Muestra el formulario vacío para crear un nuevo Mangaka.
     */
    @GetMapping("/admin/mangakas/new")
    public String showNewMangakaForm(Model model) {
        // Aseguramos que se envía un objeto vacío para el th:object del formulario.
        model.addAttribute("mangaka", new Mangaka());
        return "mangaka-form";
    }

    /**
     * Muestra el formulario con datos para editar un Mangaka existente.
     */
    @GetMapping("/admin/mangakas/edit/{id}")
    public String showEditMangakaForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Mangaka> mangakaOpt = mangakaService.findById(id);

        if (mangakaOpt.isPresent()) {
            // El objeto 'mangaka' se envía al mangaka-form.html para precargar los datos.
            model.addAttribute("mangaka", mangakaOpt.get());
            return "mangaka-form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Mangaka no encontrado para edición.");
            return "redirect:/admin/mangakas";
        }
    }


    /**
     * Procesa la creación o actualización (POST) desde el formulario.
     */
    @PostMapping("/admin/mangakas/save")
    public String saveMangaka(@Valid @ModelAttribute("mangaka") Mangaka mangaka,
                              BindingResult result,
                              RedirectAttributes attributes) {

        if (result.hasErrors()) {
            // Si hay errores de validación, regresa a la plantilla mangaka-form para mostrar los errores.
            return "mangaka-form";
        }

        try {
            boolean isNew = (mangaka.getId() == null);
            // Llama al método del servicio para guardar o actualizar.
            mangakaService.guardarMangaka(mangaka);

            String mensaje = isNew
                    ? "Mangaka creado exitosamente."
                    : "Mangaka actualizado exitosamente.";

            attributes.addFlashAttribute("successMessage", mensaje);

            // Redirige a la lista de gestión después de guardar.
            return "redirect:/admin/mangakas";

        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "Ocurrió un error inesperado al guardar el mangaka: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/mangakas";
        }
    }

    @PostMapping("/admin/mangakas/delete/{id}")
    public String deleteMangaka(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            mangakaService.deleteById(id);
            attributes.addFlashAttribute("successMessage", "Mangaka eliminado correctamente.");
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "Error al eliminar el Mangaka. Puede estar asociado a otros elementos.");
        }
        return "redirect:/admin/mangakas";
    }
}