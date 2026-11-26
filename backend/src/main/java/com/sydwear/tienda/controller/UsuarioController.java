
package com.sydwear.tienda.controller;

import com.sydwear.tienda.dto.PerfilDto;
import com.sydwear.tienda.dto.RolDto;
import com.sydwear.tienda.dto.UsuarioDto;
import com.sydwear.tienda.model.Usuario;
import com.sydwear.tienda.service.CatalogoDatosService;
import com.sydwear.tienda.service.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class UsuarioController {

    private final UsuarioService servicio;
    private final CatalogoDatosService datos;

    public UsuarioController(UsuarioService servicio,
                             CatalogoDatosService datos) {
        this.servicio = servicio;
        this.datos = datos;
    }

    // Buscamos el id del usuario que ha iniciado sesion
    private Long usuarioActual() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        for (Usuario usuario : datos.getUsuarios()) {

            if (usuario.getEmail().equalsIgnoreCase(email)) {
                return usuario.getId();
            }
        }

        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Usuario no encontrado"
        );
    }

    // Mostramos nuestro perfil
    @GetMapping("/api/usuarios/me")
    public UsuarioDto miPerfil() {

        return servicio.detalle(usuarioActual());
    }

    // Modificamos nuestro perfil
    @PutMapping("/api/usuarios/me")
    public UsuarioDto modificarPerfil(@RequestBody PerfilDto perfil) {

        return servicio.modificarPerfil(usuarioActual(), perfil);
    }

    // Mostramos todos los usuarios al administrador
    @GetMapping("/api/admin/usuarios")
    public List<UsuarioDto> listarUsuarios() {

        return servicio.listar();
    }

    // Cambiamos el rol de un usuario
    @PutMapping("/api/admin/usuarios/{id}/rol")
    public UsuarioDto cambiarRol(
            @PathVariable Long id,
            @RequestBody RolDto rol) {

        return servicio.cambiarRol(id, rol);
    }
}
