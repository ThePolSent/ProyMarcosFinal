package com.example.ProyectoMarcos.controller;

import com.example.ProyectoMarcos.model.Mangaka;
import com.example.ProyectoMarcos.model.Manga;
import com.example.ProyectoMarcos.service.MangakaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.Optional;

@Controller
public class MangakaController {

    private final MangakaService mangakaService;

    public MangakaController(MangakaService mangakaService) {
        this.mangakaService = mangakaService;
    }

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
}