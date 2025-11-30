package com.example.ProyectoMarcos.service;

import com.example.ProyectoMarcos.model.Manga;
import com.example.ProyectoMarcos.model.Noticia; // Asumimos que Noticia está en el paquete model
import com.example.ProyectoMarcos.model.Mangaka;
import com.example.ProyectoMarcos.repository.MangaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class MangaService {

    private final MangaRepository mangaRepository;

    @Autowired
    public MangaService(MangaRepository mangaRepository) {
        this.mangaRepository = mangaRepository;
    }

    public List<Manga> obtenerTodos() {
        return mangaRepository.findAll();
    }

    public Optional<Manga> buscarPorId(Long id) {
        return mangaRepository.findById(id);
    }

    public Manga guardarManga(Manga manga) {
        return mangaRepository.save(manga);
    }

    public void eliminarManga(Long id) {
        mangaRepository.deleteById(id);
    }

    public List<Manga> buscarPorGenero(String genero) {
        return mangaRepository.findByGeneroIgnoreCase(genero);
    }

    /**
     * Busca mangas cuyo título o nombre de autor contenga la cadena de consulta.
     * @param query La cadena de búsqueda ingresada por el usuario.
     * @return Lista de Mangas que coinciden con la consulta.
     */
    public List<Manga> buscarPorQuery(String query) {
        return mangaRepository.findByTituloContainingIgnoreCaseOrAutor_NombreContainingIgnoreCase(query, query);
    }

    public List<String> getUniqueGeneros() {
        return mangaRepository.findAll().stream()
                .map(Manga::getGenero)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Mangaka> getAllMangakas() {
        return mangaRepository.findAll().stream()
                .map(Manga::getAutor)

                .distinct()
                .collect(Collectors.toList());
    }

    public Optional<Mangaka> findMangakaById(Long id) {
        return getAllMangakas().stream()
                .filter(a -> a.getId() != null && a.getId().equals(id))
                .findFirst();
    }

    public List<Noticia> findNoticiasCarousel() {
        List<Manga> mangasNoticia = mangaRepository.findAll().stream()
                .filter(Manga::isEs_noticia)
                .collect(Collectors.toList());

        List<Noticia> noticias = new ArrayList<>();
        for (Manga manga : mangasNoticia) {
            noticias.add(new Noticia(
                    manga.getId(),
                    manga.getTitulo(),
                    manga.getSinopsis_noticia(),
                    manga.getPortada_noticia(),
                    manga.getId()
            ));
        }

        return noticias;
    }
}
