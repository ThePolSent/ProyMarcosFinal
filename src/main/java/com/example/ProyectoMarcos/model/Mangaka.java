package com.example.ProyectoMarcos.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate; // Aunque ya no lo usemos, Spring lo necesita para @DateTimeFormat
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@Entity
@Table(name = "mangakas")
public class Mangaka {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La nacionalidad es obligatoria")
    @Size(max = 50, message = "La nacionalidad no puede exceder los 50 caracteres")
    @Column(nullable = false, length = 50)
    private String nacionalidad;

    // ⭐ CAMBIOS CLAVE: Vuelve a String para leer VARCHAR/TEXT de la DB.
    // Usamos @DateTimeFormat para que Spring binding sepa qué formato esperar del HTML.
    @NotBlank(message = "La fecha de nacimiento es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_nacimiento", nullable = false)
    private String fecha_nacimiento; // ⭐ CAMBIO: Vuelve a ser String

    @Min(value = 14, message = "La edad mínima es 14 años")
    @Max(value = 120, message = "La edad máxima es 120 años")
    private int edad;

    @Size(max = 100, message = "El lugar de nacimiento no puede exceder los 100 caracteres")
    @Column(length = 100)
    private String lugar_nacimiento;

    @Column(name = "obras_json", columnDefinition = "TEXT")
    private String obrasJson;

    @NotBlank(message = "La biografía es obligatoria")
    @Size(max = 5000, message = "La biografía no puede exceder los 5000 caracteres")
    @Lob
    private String biografia;

    @Size(max = 255, message = "La URL de la foto no puede exceder los 255 caracteres")
    @Column(length = 255)
    private String foto;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Manga> mangas = new ArrayList<>();


    public Mangaka() {}

    // ⭐ Constructores actualizados para usar String
    public Mangaka(Long id, String nombre, String nacionalidad, String fecha_nacimiento, int edad, String lugar_nacimiento, String biografia, String foto) {
        this.id = id;
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
        this.fecha_nacimiento = fecha_nacimiento;
        this.edad = edad;
        this.lugar_nacimiento = lugar_nacimiento;
        this.biografia = biografia;
        this.foto = foto;
    }

    // ⭐ Constructor actualizado para usar String
    public Mangaka(String nombre, String nacionalidad, String fecha_nacimiento, int edad, String lugar_nacimiento, String biografia, String foto) {
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
        this.fecha_nacimiento = fecha_nacimiento;
        this.edad = edad;
        this.lugar_nacimiento = lugar_nacimiento;
        this.biografia = biografia;
        this.foto = foto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    // ⭐ Getter actualizado para usar String
    public String getFecha_nacimiento() { return fecha_nacimiento; }
    // ⭐ Setter actualizado para usar String
    public void setFecha_nacimiento(String fecha_nacimiento) { this.fecha_nacimiento = fecha_nacimiento; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public String getLugar_nacimiento() { return lugar_nacimiento; }
    public void setLugar_nacimiento(String lugar_nacimiento) { this.lugar_nacimiento = lugar_nacimiento; }
    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public List<Manga> getMangas() { return mangas; }
    public void setMangas(List<Manga> mangas) { this.mangas = mangas; }

    public List<String> getObras() {
        if (obrasJson == null || obrasJson.isEmpty()) {
            return List.of();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(obrasJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Arrays.asList("Error al cargar obras");
        }
    }

    public void setObras(List<String> obras) {
        if (obras == null) {
            this.obrasJson = null;
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.obrasJson = mapper.writeValueAsString(obras);
        } catch (Exception e) {
            e.printStackTrace();
            this.obrasJson = "[]"; // Fallback
        }
    }

    public String getObrasJson() { return obrasJson; }
    public void setObrasJson(String obrasJson) { this.obrasJson = obrasJson; }
}