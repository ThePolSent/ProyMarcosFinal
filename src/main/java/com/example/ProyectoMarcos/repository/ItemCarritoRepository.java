package com.example.ProyectoMarcos.repository;

import com.example.ProyectoMarcos.model.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    List<ItemCarrito> findByUsuarioId(Long usuarioId);

    Optional<ItemCarrito> findByUsuarioIdAndMangaId(Long usuarioId, Long mangaId);
}