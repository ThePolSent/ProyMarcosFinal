package com.example.ProyectoMarcos.repository;

import com.example.ProyectoMarcos.model.Manga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MangaRepository extends JpaRepository<Manga, Long> {

    // Método para buscar por género (se mantiene)
    List<Manga> findByGeneroIgnoreCase(String genero);

    // 🚨 NUEVO MÉTODO DE BÚSQUEDA (SOLO POR TÍTULO)
    // El MangaService.buscarPorQuery() ahora llama a este método.
    List<Manga> findByTituloContainingIgnoreCase(String titulo);

    // 🗑️ NOTA: El método findByTituloContainingIgnoreCaseOrAutor_NombreContainingIgnoreCase
    // ha sido ELIMINADO de este archivo para evitar el error y simplificar la búsqueda.
}