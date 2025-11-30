package com.example.ProyectoMarcos.model;

/**
 * Clase de modelo utilizada para representar las noticias del carrusel,
 * que se derivan de la información marcada en la entidad Manga.
 *
 * NOTA: Esta no es una entidad JPA, es un POJO simple para mover datos (DTO de Noticia).
 */
public class Noticia {
    private Long id;
    private String titulo;
    private String descripcion;
    private String portada;
    private Long id_manga; // Referencia al Manga original

    public Noticia() {
    }

    public Noticia(Long id, String titulo, String descripcion, String portada, Long id_manga) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.portada = portada;
        this.id_manga = id_manga;
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getPortada() { return portada; }
    public Long getId_manga() { return id_manga; }

    public void setId(Long id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setPortada(String portada) { this.portada = portada; }
    public void setId_manga(Long id_manga) { this.id_manga = id_manga; }
}