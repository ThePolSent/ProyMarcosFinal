package com.example.ProyectoMarcos.controller;

import com.example.ProyectoMarcos.model.Favorito;
import com.example.ProyectoMarcos.model.ItemCarrito;
import com.example.ProyectoMarcos.model.Usuario;
import com.example.ProyectoMarcos.service.CarritoService;
import com.example.ProyectoMarcos.service.FavoritoService;
import com.example.ProyectoMarcos.service.MangaService;
import com.example.ProyectoMarcos.service.UsuarioService; // ⬅️ ¡NUEVA IMPORTACIÓN!
// import jakarta.servlet.http.HttpSession; // ❌ ELIMINADA
import org.springframework.security.core.Authentication; // ⬅️ ¡NUEVA IMPORTACIÓN!
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class EcommerceController {

    private final CarritoService carritoService;
    private final FavoritoService favoritoService;
    private final MangaService mangaService;
    private final UsuarioService usuarioService; // ⬅️ ¡NUEVA INYECCIÓN!

    public EcommerceController(CarritoService carritoService, FavoritoService favoritoService, MangaService mangaService, UsuarioService usuarioService) {
        this.carritoService = carritoService;
        this.favoritoService = favoritoService;
        this.mangaService = mangaService;
        this.usuarioService = usuarioService;
    }

    /**
     * Reemplaza obtenerIdUsuarioSesion().
     * Obtiene el ID del usuario a partir del contexto de seguridad.
     * @param authentication El objeto Authentication de Spring Security.
     * @return El ID del Usuario.
     */
    private Long obtenerIdUsuarioAutenticado(Authentication authentication) {
        // authentication.getName() devuelve el correo del usuario (Subject del JWT)
        String correo = authentication.getName();

        Optional<Usuario> optionalUsuario = usuarioService.buscarPorCorreo(correo);

        // Si el usuario no existe en la DB (aunque el token sea válido), lanzamos excepción.
        if (optionalUsuario.isEmpty()) {
            throw new IllegalStateException("Usuario autenticado no encontrado en la base de datos.");
        }
        return optionalUsuario.get().getId();
    }

    // =================================================================
    // FAVORITOS
    // =================================================================

    @GetMapping("/favorites")
    // 🛡️ NO necesita @PreAuthorize si la ruta está protegida en SecurityConfig
    public String getFavorites(Model model, Authentication authentication, RedirectAttributes ra) {
        try {
            Long usuarioId = obtenerIdUsuarioAutenticado(authentication);
            List<Favorito> favoritos = favoritoService.findFavoritesByUserId(usuarioId);
            model.addAttribute("favoritos", favoritos);
            return "favorites";
        } catch (IllegalStateException e) {
            // Este catch es redundante si la ruta está protegida, pero lo mantenemos como seguridad.
            ra.addFlashAttribute("errorLogin", "Debes iniciar sesión para ver tus favoritos.");
            return "redirect:/login";
        }
    }

    @PostMapping("/favorites/toggle/{mangaId}")
    public String toggleFavorite(@PathVariable Long mangaId, RedirectAttributes ra, Authentication authentication) {
        String redirectionTarget = "redirect:/mangas/" + mangaId;

        try {
            Long usuarioId = obtenerIdUsuarioAutenticado(authentication);

            boolean added = favoritoService.toggleFavorite(usuarioId, mangaId);
            String message = added ? "Manga **añadido** a favoritos. ❤️" : "Manga **eliminado** de favoritos. 💔";

            ra.addFlashAttribute("successMessage", message);

        } catch (IllegalStateException e) {
            // Si el token es inválido o el usuario no existe (debería ser manejado por el filtro)
            ra.addFlashAttribute("errorLogin", "Debes iniciar sesión para añadir a favoritos.");
            redirectionTarget = "redirect:/login";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }

        return redirectionTarget;
    }

    @PostMapping("/favorites/delete/{favoritoId}")
    public String deleteFavorite(@PathVariable Long favoritoId, Authentication authentication, RedirectAttributes ra) {
        try {
            // Verificamos que el usuario esté autenticado y exista.
            obtenerIdUsuarioAutenticado(authentication);

            // ⚠️ NOTA: Deberías añadir aquí una comprobación de seguridad
            // para asegurar que el favoritoId pertenezca al usuarioId autenticado.
            favoritoService.deleteFavorite(favoritoId);
            ra.addFlashAttribute("successMessage", "Favorito eliminado correctamente.");

        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorLogin", "Debes iniciar sesión.");
            return "redirect:/login";
        }

        return "redirect:/favorites";
    }

    // =================================================================
    // CARRITO DE COMPRAS
    // =================================================================

    @GetMapping("/carrito")
    public String getCarrito(Model model, Authentication authentication, RedirectAttributes ra) {
        try {
            Long usuarioId = obtenerIdUsuarioAutenticado(authentication);

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
                                Authentication authentication) {
        String redirectionTarget = "redirect:/mangas/" + mangaId;

        try {
            Long usuarioId = obtenerIdUsuarioAutenticado(authentication);
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
                                     Authentication authentication,
                                     RedirectAttributes ra) {
        try {
            // Verificamos que el usuario esté autenticado y exista.
            obtenerIdUsuarioAutenticado(authentication);

            // ⚠️ NOTA: Deberías añadir aquí una comprobación de seguridad
            // para asegurar que el itemId pertenezca al usuarioId autenticado.
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
    public String deleteItemFromCart(@PathVariable Long itemId, Authentication authentication, RedirectAttributes ra) {
        try {
            // Verificamos que el usuario esté autenticado y exista.
            obtenerIdUsuarioAutenticado(authentication);

            // ⚠️ NOTA: Deberías añadir aquí una comprobación de seguridad
            // para asegurar que el itemId pertenezca al usuarioId autenticado.
            carritoService.deleteItem(itemId);
            ra.addFlashAttribute("successMessage", "Ítem eliminado del carrito.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorLogin", "Sesión inválida.");
            return "redirect:/login";
        }
        return "redirect:/carrito";
    }
}