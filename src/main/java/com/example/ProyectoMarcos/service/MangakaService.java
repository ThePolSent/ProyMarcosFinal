package com.example.ProyectoMarcos.service;

import com.example.ProyectoMarcos.model.Manga;
import com.example.ProyectoMarcos.model.Mangaka;
import com.example.ProyectoMarcos.repository.MangaRepository;
import com.example.ProyectoMarcos.repository.MangakaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MangakaService {

    private final MangakaRepository mangakaRepository;
    private final MangaRepository mangaRepository;

    public MangakaService(MangakaRepository mangakaRepository, MangaRepository mangaRepository) {
        this.mangakaRepository = mangakaRepository;
        this.mangaRepository = mangaRepository;
    }

    @Transactional
    public Mangaka guardarMangaka(Mangaka mangaka) {
        return mangakaRepository.save(mangaka);
    }

    public List<Mangaka> findAll() {
        // Carga todos los Mangakas directamente desde la base de datos
        return mangakaRepository.findAll();
    }

    public Optional<Mangaka> findById(Long id) {
        // Busca un Mangaka por ID directamente en la base de datos
        return mangakaRepository.findById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        mangakaRepository.deleteById(id);
    }

    public long count() {
        return mangakaRepository.count();
    }

    public List<Manga> findMangaByAuthorId(Long id) {

        return mangaRepository.findAll().stream()
                .filter(manga -> manga.getAutor() != null && manga.getAutor().getId().equals(id))
                .collect(Collectors.toList());
    }
}