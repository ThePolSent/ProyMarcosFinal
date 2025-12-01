package com.example.ProyectoMarcos.model;

import jakarta.persistence.*;
import java.util.Objects; // Importar Objects

@Entity
@Table(name = "favoritos")
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación Many-to-One con Usuario
    // mappedBy en Usuario: "usuario"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relación Many-to-One con Manga
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manga_id", nullable = false)
    private Manga manga;

    public Favorito() {}

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Manga getManga() { return manga; }
    public void setManga(Manga manga) { this.manga = manga; }

    // --- Métodos equals y hashCode (Buenas Prácticas) ---
    /**
     * Implementación de equals para comparar si dos objetos Favorito son iguales.
     * Es crucial para colecciones Set. Usamos el ID de la base de datos si existe,
     * o los campos que definen la unicidad (usuario y manga) si aún no ha sido persistido.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Favorito favorito = (Favorito) o;

        // Si el ID existe, lo usamos.
        if (id != null) {
            return Objects.equals(id, favorito.id);
        }

        // Si el ID no existe (objeto nuevo), comparamos por usuario y manga.
        return Objects.equals(usuario, favorito.usuario) &&
                Objects.equals(manga, favorito.manga);
    }

    /**
     * Implementación de hashCode. Debe ser consistente con equals().
     */
    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(usuario, manga);
    }
}