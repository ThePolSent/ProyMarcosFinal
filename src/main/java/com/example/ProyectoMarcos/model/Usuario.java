package com.example.ProyectoMarcos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Column(nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Column(nullable = false, length = 50)
    private String apellido;

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 4, max = 30, message = "El username debe tener entre 4 y 30 caracteres")
    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String contrasena;

    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private String rol = "USER"; // Valor por defecto

    /**
     * Relación One-to-Many con los ítems del carrito.
     * La cascada asegura que los ítems del carrito se eliminen con el usuario.
     */
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ItemCarrito> itemsCarrito = new HashSet<>();

    /**
     * NUEVA RELACIÓN: Relación One-to-Many con Favoritos.
     * Esta es la clave para resolver el nuevo error 1451.
     * cascade=CascadeType.ALL asegura que los favoritos se eliminen con el usuario.
     */
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Favorito> favoritos = new HashSet<>();


    public Usuario() {
    }

    // --- GETTERS ---
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getUsername() {
        return username;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getRol() {
        return rol;
    }

    public Set<ItemCarrito> getItemsCarrito() {
        return itemsCarrito;
    }

    public Set<Favorito> getFavoritos() {
        return favoritos;
    }


    // --- SETTERS ---
    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setItemsCarrito(Set<ItemCarrito> itemsCarrito) {
        this.itemsCarrito = itemsCarrito;
    }

    public void setFavoritos(Set<Favorito> favoritos) {
        this.favoritos = favoritos;
    }
}