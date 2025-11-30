package com.example.ProyectoMarcos.controller;

import com.example.ProyectoMarcos.model.Manga;
import com.example.ProyectoMarcos.service.MangaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CategoryController {

    @Autowired
    private MangaService mangaService;

    @GetMapping("/categorias")
    public String categorias(Model model) {
        model.addAttribute("generos", mangaService.getUniqueGeneros());
        return "categorias";
    }

    @GetMapping("/mangas/genero/{genero}")
    public String getMangasByGenero(@PathVariable String genero, Model model) {
        // CORRECCIÓN: Llamar a buscarPorGenero en lugar de getMangasByGenero
        model.addAttribute("mangas", mangaService.buscarPorGenero(genero));
        model.addAttribute("generoActual", genero);
        return "mangas-por-genero";
    }
}