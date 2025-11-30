package com.example.ProyectoMarcos.controller;

import com.example.ProyectoMarcos.model.Favorito;
import com.example.ProyectoMarcos.model.ItemCarrito;
import com.example.ProyectoMarcos.model.Usuario;
import com.example.ProyectoMarcos.service.CarritoService;
import com.example.ProyectoMarcos.service.FavoritoService;
import com.example.ProyectoMarcos.service.MangaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class EcommerceController {

    private final CarritoService carritoService;
    private final FavoritoService favoritoService;
    private final MangaService mangaService;

    public EcommerceController(CarritoService carritoService, FavoritoService favoritoService, MangaService mangaService) {
        this.carritoService = carritoService;
        this.favoritoService = favoritoService;
        this.mangaService = mangaService;
    }

    private Long obtenerIdUsuarioSesion(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            // Lanza una excepción si no hay sesión, será capturada por el try-catch
            throw new IllegalStateException("El usuario no ha iniciado sesión.");
        }
        return usuario.getId();
    }

    @GetMapping("/favorites")
    public String getFavorites(Model model, HttpSession session, RedirectAttributes ra) {
        try {
            Long usuarioId = obtenerIdUsuarioSesion(session);
            List<Favorito> favoritos = favoritoService.findFavoritesByUserId(usuarioId);
            model.addAttribute("favoritos", favoritos);
            return "favorites";
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorLogin", "Debes iniciar sesión para ver tus favoritos.");
            return "redirect:/login"; // Redirigir si no hay sesión
        }
    }

    @PostMapping("/favorites/toggle/{mangaId}")
    public String toggleFavorite(@PathVariable Long mangaId, RedirectAttributes ra, HttpSession session) {
        String redirectionTarget = "redirect:/mangas/" + mangaId;

        try {
            Long usuarioId = obtenerIdUsuarioSesion(session);

            boolean added = favoritoService.toggleFavorite(usuarioId, mangaId);
            String message = added ? "Manga **añadido** a favoritos. ❤️" : "Manga **eliminado** de favoritos. 💔";

            ra.addFlashAttribute("successMessage", message);

        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorLogin", "Debes iniciar sesión para añadir a favoritos.");
            redirectionTarget = "redirect:/login";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }

        return redirectionTarget;
    }

    @PostMapping("/favorites/delete/{favoritoId}")
    public String deleteFavorite(@PathVariable Long favoritoId, HttpSession session, RedirectAttributes ra) {
        try {
            obtenerIdUsuarioSesion(session); // Solo para verificar sesión

            // Lógica de Seguridad omitida por simplicidad, pero se debe verificar que el Favorito pertenezca al usuario.
            favoritoService.deleteFavorite(favoritoId);
            ra.addFlashAttribute("successMessage", "Favorito eliminado correctamente.");

        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorLogin", "Debes iniciar sesión.");
            return "redirect:/login";
        }

        // Vuelve a la lista actualizada de favoritos
        return "redirect:/favorites";
    }

    @GetMapping("/carrito")
    public String getCarrito(Model model, HttpSession session, RedirectAttributes ra) {
        try {
            Long usuarioId = obtenerIdUsuarioSesion(session);

            List<ItemCarrito> items = carritoService.findItemsByUserId(usuarioId);
            double subtotal = carritoService.calculateSubtotal(usuarioId);
            double total = carritoService.calculateTotal(usuarioId);

            model.addAttribute("items", items);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("total", total);
            return "carrito";
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorLogin", "Debes iniciar sesión para ver tu carrito.");
            return "redirect:/login";
        }
    }

    @PostMapping("/carrito/add/{mangaId}")
    public String addItemToCart(@PathVariable Long mangaId,
                                @RequestParam(defaultValue = "1") int quantity,
                                RedirectAttributes ra,
                                HttpSession session) {
        String redirectionTarget = "redirect:/mangas/" + mangaId;

        try {
            Long usuarioId = obtenerIdUsuarioSesion(session);
            carritoService.addItemToCart(usuarioId, mangaId, quantity);
            ra.addFlashAttribute("successMessage", "Manga añadido al carrito. 🛍️");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorLogin", "Debes iniciar sesión para añadir al carrito.");
            redirectionTarget = "redirect:/login";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return redirectionTarget;
    }

    @PostMapping("/carrito/update")
    public String updateItemQuantity(@RequestParam Long itemId,
                                     @RequestParam int quantity,
                                     HttpSession session,
                                     RedirectAttributes ra) {
        try {
            obtenerIdUsuarioSesion(session);

            carritoService.updateItemQuantity(itemId, quantity);
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorLogin", "Sesión inválida.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/carrito";
    }


    @PostMapping("/carrito/delete/{itemId}")
    public String deleteItemFromCart(@PathVariable Long itemId, HttpSession session, RedirectAttributes ra) {
        try {
            obtenerIdUsuarioSesion(session);

            carritoService.deleteItem(itemId);
            ra.addFlashAttribute("successMessage", "Ítem eliminado del carrito.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorLogin", "Sesión inválida.");
            return "redirect:/login";
        }
        return "redirect:/carrito";
    }
}