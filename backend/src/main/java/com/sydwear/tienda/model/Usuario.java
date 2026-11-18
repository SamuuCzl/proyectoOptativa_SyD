package com.sydwear.tienda.model;

import java.time.LocalDateTime;

public class Usuario {

    private Long id;
    private String email;
    private String nombre;
    private String direccionEnvio;
    private Rol rol;
    private LocalDateTime fechaAlta;
    private String passwordHash;

    public Usuario() {
    }

    public Usuario(Long id, String email, String nombre,
                   String direccionEnvio, Rol rol,
                   LocalDateTime fechaAlta, String passwordHash) {

        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.direccionEnvio = direccionEnvio;
        this.rol = rol;
        this.fechaAlta = fechaAlta;
        this.passwordHash = passwordHash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public LocalDateTime getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDateTime fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
