package com.example.ProyectoMarcos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "capitulos")
public class Capitulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título del capítulo es obligatorio")
    @Size(max = 150, message = "El título no puede exceder los 150 caracteres")
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotNull(message = "El número de capítulo es obligatorio")
    @Min(value = 1, message = "El número de capítulo debe ser al menos 1")
    @Column(nullable = false)
    private Integer numero; // Número de capítulo

    @Lob
    private String contenido_path;

    private LocalDate fecha_lanzamiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manga_id", nullable = false) // Un capítulo siempre debe pertenecer a un manga
    @JsonIgnore
    @NotNull(message = "El capítulo debe estar asociado a un Manga")
    private Manga manga;

    public Capitulo() {}

    // Constructor completo
    public Capitulo(String titulo, Integer numero, String contenido_path, LocalDate fecha_lanzamiento, Manga manga) {
        this.titulo = titulo;
        this.numero = numero;
        this.contenido_path = contenido_path;
        this.fecha_lanzamiento = fecha_lanzamiento;
        this.manga = manga;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getContenido_path() {
        return contenido_path;
    }

    public void setContenido_path(String contenido_path) {
        this.contenido_path = contenido_path;
    }

    public LocalDate getFecha_lanzamiento() {
        return fecha_lanzamiento;
    }

    public void setFecha_lanzamiento(LocalDate fecha_lanzamiento) {
        this.fecha_lanzamiento = fecha_lanzamiento;
    }

    public Manga getManga() {
        return manga;
    }

    public void setManga(Manga manga) {
        this.manga = manga;
    }
}