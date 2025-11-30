package com.example.ProyectoMarcos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "mangas")
public class Manga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede exceder los 150 caracteres")
    @Column(nullable = false, length = 150)
    private String titulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    @NotNull(message = "El autor es obligatorio")
    private Mangaka autor;

    @NotBlank(message = "El género es obligatorio")
    @Size(max = 50, message = "El género no puede exceder los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String genero;

    @Size(max = 255, message = "La URL de la portada no puede exceder los 255 caracteres")
    @Column(length = 255)
    private String portada;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 2000, message = "La descripción no puede exceder los 2000 caracteres")
    @Lob
    private String descripcion;

    @Min(value = 0, message = "El precio no puede ser negativo")
    private double precio;

    private boolean es_noticia = false;

    @Size(max = 500, message = "La sinopsis de noticia no puede exceder los 500 caracteres")
    @Column(length = 500)
    private String sinopsis_noticia;

    @Size(max = 255, message = "La URL de la portada de noticia no puede exceder los 255 caracteres")
    @Column(length = 255)
    private String portada_noticia;

    @OneToMany(mappedBy = "manga", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Capitulo> capitulos;

    public Manga() {}

    public Manga(String titulo, Mangaka autor, String genero, String portada, String descripcion, double precio, boolean es_noticia, String sinopsis_noticia, String portada_noticia, List<Capitulo> capitulos) {
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.portada = portada;
        this.descripcion = descripcion;
        this.precio = precio;
        this.es_noticia = es_noticia;
        this.sinopsis_noticia = sinopsis_noticia;
        this.portada_noticia = portada_noticia;
        this.capitulos = capitulos;
    }

    public Manga(String titulo, String genero, double precio, String portada, String descripcion, boolean es_noticia, String sinopsis_noticia, String portada_noticia, Mangaka autor) {
        this.titulo = titulo;
        this.genero = genero;
        this.precio = precio;
        this.portada = portada;
        this.descripcion = descripcion;
        this.es_noticia = es_noticia;
        this.sinopsis_noticia = sinopsis_noticia;
        this.portada_noticia = portada_noticia;
        this.autor = autor;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Mangaka getAutor() { return autor; }
    public void setAutor(Mangaka autor) { this.autor = autor; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public String getPortada() { return portada; }
    public void setPortada(String portada) { this.portada = portada; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public boolean isEs_noticia() { return es_noticia; }
    public void setEs_noticia(boolean es_noticia) { this.es_noticia = es_noticia; }
    public String getSinopsis_noticia() { return sinopsis_noticia; }
    public void setSinopsis_noticia(String sinopsis_noticia) { this.sinopsis_noticia = sinopsis_noticia; }
    public String getPortada_noticia() { return portada_noticia; }
    public void setPortada_noticia(String portada_noticia) { this.portada_noticia = portada_noticia; }
    public List<Capitulo> getCapitulos() { return capitulos; }
    public void setCapitulos(List<Capitulo> capitulos) { this.capitulos = capitulos; }

    public void addCapitulo(Capitulo capitulo) {
        this.capitulos.add(capitulo);
        capitulo.setManga(this);
    }

    public void removeCapitulo(Capitulo capitulo) {
        this.capitulos.remove(capitulo);
        capitulo.setManga(null);
    }
}