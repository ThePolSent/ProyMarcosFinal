package com.example.ProyectoMarcos.controller;

import com.example.ProyectoMarcos.model.Capitulo;
import com.example.ProyectoMarcos.model.Manga;
import com.example.ProyectoMarcos.service.MangaService;
import com.example.ProyectoMarcos.service.NoticiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;
import java.util.List;

@Controller
public class MangaController {

    private final MangaService mangaService;
    private final NoticiaService noticiaService;

    public MangaController(MangaService mangaService, NoticiaService noticiaService) {
        this.mangaService = mangaService;
        this.noticiaService = noticiaService;
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
        model.addAttribute("generos", mangaService.getUniqueGeneros()); // Para futuros filtros/secciones
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

        model.addAttribute("tituloPagina", "Resultados de búsqueda: " + query);

        return "manga_catalog";
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
}