package com.example.ProyectoMarcos.repository;

import com.example.ProyectoMarcos.model.Manga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MangaRepository extends JpaRepository<Manga, Long> {

    List<Manga> findByGeneroIgnoreCase(String genero);

    List<Manga> findByTituloContainingIgnoreCaseOrAutor_NombreContainingIgnoreCase(String tituloQuery, String autorQuery);
}
