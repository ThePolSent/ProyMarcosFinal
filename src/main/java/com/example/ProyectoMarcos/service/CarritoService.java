package com.example.ProyectoMarcos.service;

import com.example.ProyectoMarcos.model.ItemCarrito;
import com.example.ProyectoMarcos.model.Manga;
import com.example.ProyectoMarcos.model.Usuario;
import com.example.ProyectoMarcos.repository.ItemCarritoRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {

    private final ItemCarritoRepository carritoRepository;
    private final MangaService mangaService;
    private final UsuarioService usuarioService;

    public CarritoService(ItemCarritoRepository carritoRepository, MangaService mangaService, UsuarioService usuarioService) {
        this.carritoRepository = carritoRepository;
        this.mangaService = mangaService;
        this.usuarioService = usuarioService;
    }


    public List<ItemCarrito> findItemsByUserId(Long usuarioId) {
        List<ItemCarrito> items = carritoRepository.findByUsuarioId(usuarioId);

        return items != null ? items : Collections.emptyList();
    }


    public ItemCarrito addItemToCart(Long usuarioId, Long mangaId, int quantity) {
        Optional<Manga> mangaOpt = mangaService.buscarPorId(mangaId);
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioId);

        if (mangaOpt.isEmpty() || usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Manga o Usuario no encontrado.");
        }
        Manga manga = mangaOpt.get();
        Usuario usuario = usuarioOpt.get();

        Optional<ItemCarrito> existingItem = carritoRepository.findByUsuarioIdAndMangaId(usuarioId, mangaId);

        ItemCarrito item;
        if (existingItem.isPresent()) {
            // Actualizar cantidad
            item = existingItem.get();
            item.setCantidad(item.getCantidad() + quantity);
        } else {
            // Nuevo ítem
            item = new ItemCarrito();
            item.setUsuario(usuario);
            item.setManga(manga);
            item.setCantidad(quantity);
        }

        return carritoRepository.save(item);
    }

    public ItemCarrito updateItemQuantity(Long itemId, int quantity) {
        if (quantity < 1) {
            deleteItem(itemId);
            return null;
        }

        Optional<ItemCarrito> itemOpt = carritoRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Ítem de carrito no encontrado con ID: " + itemId);
        }

        ItemCarrito item = itemOpt.get();
        item.setCantidad(quantity);
        return carritoRepository.save(item);
    }


    public void deleteItem(Long itemId) {
        carritoRepository.deleteById(itemId);
    }


    public double calculateSubtotal(Long usuarioId) {
        return findItemsByUserId(usuarioId).stream()
                .mapToDouble(item -> item.getCantidad() * item.getManga().getPrecio())
                .sum();
    }


    public double calculateTotal(Long usuarioId) {
        double subtotal = calculateSubtotal(usuarioId);
        final double SHIPPING_COST = 10.00; // Costo de envío fijo
        return subtotal > 0 ? subtotal + SHIPPING_COST : 0;
    }
}