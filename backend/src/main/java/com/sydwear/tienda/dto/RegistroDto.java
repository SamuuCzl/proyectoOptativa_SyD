
package com.sydwear.tienda.dto;

public class RegistroDto {

    private String email;
    private String nombre;
    private String direccionEnvio;
    private String password;

    public RegistroDto() {
    }

    public RegistroDto(String email, String nombre,
                       String direccionEnvio, String password) {
        this.email = email;
        this.nombre = nombre;
        this.direccionEnvio = direccionEnvio;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

