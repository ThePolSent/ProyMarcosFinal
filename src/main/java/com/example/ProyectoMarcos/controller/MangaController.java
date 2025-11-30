package com.example.ProyectoMarcos.controller;

import com.example.ProyectoMarcos.model.Capitulo;
import com.example.ProyectoMarcos.model.Manga;
import com.example.ProyectoMarcos.service.MangaService;
import com.example.ProyectoMarcos.service.NoticiaService;
import com.example.ProyectoMarcos.service.MangakaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;
import java.util.List;

@Controller
public class MangaController {

    private final MangaService mangaService;
    private final NoticiaService noticiaService;
    private final MangakaService mangakaService;

    public MangaController(MangaService mangaService, NoticiaService noticiaService, MangakaService mangakaService) {
        this.mangaService = mangaService;
        this.noticiaService = noticiaService;
        this.mangakaService = mangakaService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("mangas", mangaService.obtenerTodos());
        model.addAttribute("noticias", noticiaService.findNoticiasCarousel());
        return "index";
    }

    @GetMapping("/mangas")
    public String getCatalogoManga(Model model) {
        model.addAttribute("mangas", mangaService.obtenerTodos());
        model.addAttribute("generos", mangaService.getUniqueGeneros());
        return "manga_catalog";
    }

    /**
     * Maneja la solicitud de búsqueda desde la barra de navegación.
     * Mapeado a /mangas/search?query=texto_de_busqueda
     * @param query El término de búsqueda capturado del formulario.
     * @param model El modelo para pasar datos a la vista.
     * @return El nombre de la vista donde se muestran los resultados.
     */
    @GetMapping("/mangas/search")
    public String searchMangas(@RequestParam(name = "query") String query, Model model) {

        List<Manga> resultados = mangaService.buscarPorQuery(query);

        model.addAttribute("mangas", resultados);
        model.addAttribute("query", query); // Añadir el query para la cabecera

        // 🚨 CAMBIO: Retornar a la vista específica de resultados de búsqueda (search-results.html)
        return "search-results";
    }


    @GetMapping("/mangas/{id}")
    public String getMangaDetails(@PathVariable Long id, Model model) {
        Optional<Manga> manga = mangaService.buscarPorId(id);
        if (manga.isPresent()) {
            model.addAttribute("manga", manga.get());
            return "manga_details";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/mangas/{mangaId}/capitulo/{capituloId}")
    public String getChapter(@PathVariable Long mangaId, @PathVariable Long capituloId, Model model) {
        Optional<Manga> manga = mangaService.buscarPorId(mangaId);
        if (manga.isPresent()) {
            Optional<Capitulo> capitulo = manga.get().getCapitulos().stream()
                    .filter(c -> c.getId().equals(capituloId))
                    .findFirst();

            if (capitulo.isPresent()) {
                model.addAttribute("manga", manga.get());
                model.addAttribute("capitulo", capitulo.get());
                return "capitulo_page";
            }
        }
        return "error/404";
    }

    @GetMapping("/admin/manga/new")
    public String showMangaForm(Model model) {
        model.addAttribute("manga", new Manga());
        model.addAttribute("mangakas", mangakaService.findAll());
        return "manga-form";
    }

    @PostMapping("/admin/manga/new")
    public String saveManga(@Valid Manga manga, BindingResult result, Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            // 🚨 CAMBIO CLAVE: Si hay errores de validación, DEBEMOS recargar los mangakas
            // y retornar al formulario. Ya no usamos RedirectAttributes para el error aquí.
            model.addAttribute("mangakas", mangakaService.findAll());
            return "manga-form";
        }

        try {
            mangaService.guardarManga(manga);
            attributes.addFlashAttribute("successMessage", "Manga " + manga.getTitulo() + " guardado exitosamente.");
            return "redirect:/mangas";
        } catch (Exception e) {
            // Manejar errores de persistencia aquí.
            attributes.addFlashAttribute("errorMessage", "Ocurrió un error inesperado al guardar el manga.");
            e.printStackTrace();
            // Si el error es post-validación y queremos volver al form, también cargamos los mangakas.
            model.addAttribute("mangakas", mangakaService.findAll());
            return "manga-form";
        }
    }
}