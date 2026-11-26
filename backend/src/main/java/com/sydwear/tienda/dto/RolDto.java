package com.sydwear.tienda.dto;

import com.sydwear.tienda.model.Rol;

public class RolDto {

    private Rol rol;

    public RolDto() {
    }

    public RolDto(Rol rol) {
        this.rol = rol;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
