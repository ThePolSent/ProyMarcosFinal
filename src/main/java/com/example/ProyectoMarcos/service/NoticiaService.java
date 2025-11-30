package com.example.ProyectoMarcos.service;

import com.example.ProyectoMarcos.model.Manga;
import com.example.ProyectoMarcos.model.Noticia;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticiaService {

    private final MangaService mangaService;

    public NoticiaService(MangaService mangaService) {
        this.mangaService = mangaService;
    }

    public List<Noticia> findNoticiasCarousel() {
        // CORRECCIÓN: De findAll() a obtenerTodos()
        return mangaService.obtenerTodos().stream()
                .filter(Manga::isEs_noticia)
                .map(this::convertToNoticia)
                .collect(Collectors.toList());
    }

    private Noticia convertToNoticia(Manga manga) {
        Noticia noticia = new Noticia();
        noticia.setTitulo(manga.getTitulo());
        noticia.setDescripcion(manga.getSinopsis_noticia());
        noticia.setPortada(manga.getPortada_noticia()); // <-- ¡Cambiado aquí!
        noticia.setId_manga(manga.getId());
        return noticia;
    }
}