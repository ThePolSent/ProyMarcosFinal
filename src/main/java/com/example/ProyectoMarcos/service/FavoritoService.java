package com.example.ProyectoMarcos.service;

import com.example.ProyectoMarcos.model.Favorito;
import com.example.ProyectoMarcos.model.Manga;
import com.example.ProyectoMarcos.model.Usuario; // Importar Usuario
import com.example.ProyectoMarcos.repository.FavoritoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final MangaService mangaService;
    private final UsuarioService usuarioService; // NUEVO: Para obtener el objeto Usuario

    public FavoritoService(FavoritoRepository favoritoRepository, MangaService mangaService, UsuarioService usuarioService) {
        this.favoritoRepository = favoritoRepository;
        this.mangaService = mangaService;
        this.usuarioService = usuarioService;
    }

    /**
     * Obtiene todos los favoritos para el usuario actual.
     * @param usuarioId El ID de tipo Long del usuario logueado.
     */
    public List<Favorito> findFavoritesByUserId(Long usuarioId) { // CAMBIO: Long
        return favoritoRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Alterna el estado de favorito de un manga (añade o elimina).
     * @param usuarioId El ID de tipo Long del usuario logueado.
     * @return true si fue añadido, false si fue eliminado.
     */
    public boolean toggleFavorite(Long usuarioId, Long mangaId) { // CAMBIO: Long
        Optional<Favorito> existingFavorite = favoritoRepository.findByUsuarioIdAndMangaId(usuarioId, mangaId);

        if (existingFavorite.isPresent()) {
            favoritoRepository.delete(existingFavorite.get());
            return false;
        } else {
            Optional<Manga> mangaOpt = mangaService.buscarPorId(mangaId); // Usar buscarPorId del MangaService
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioId); // Obtener el objeto Usuario

            if (mangaOpt.isEmpty() || usuarioOpt.isEmpty()) {
                throw new IllegalArgumentException("Manga o Usuario no encontrado.");
            }

            Favorito favorito = new Favorito();
            favorito.setUsuario(usuarioOpt.get());
            favorito.setManga(mangaOpt.get());
            favoritoRepository.save(favorito);
            return true;
        }
    }


    public void deleteFavorite(Long favoritoId) {
        favoritoRepository.deleteById(favoritoId);
    }
}