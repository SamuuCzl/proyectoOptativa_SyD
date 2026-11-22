
package com.sydwear.tienda.service;

import com.sydwear.tienda.dto.LoginDto;
import com.sydwear.tienda.dto.PerfilDto;
import com.sydwear.tienda.dto.RegistroDto;
import com.sydwear.tienda.dto.RolDto;
import com.sydwear.tienda.dto.UsuarioDto;
import com.sydwear.tienda.model.Rol;
import com.sydwear.tienda.model.Usuario;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {

    private final CatalogoDatosService datos;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(CatalogoDatosService datos) {
        this.datos = datos;
    }

    // Pasamos un usuario a DTO sin incluir su contraseña
    private UsuarioDto dto(Usuario usuario) {
        return new UsuarioDto(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getDireccionEnvio(),
                usuario.getRol(),
                usuario.getFechaAlta()
        );
    }

    // Buscamos un usuario por su id
    private Usuario buscar(Long id) {

        for (Usuario usuario : datos.getUsuarios()) {
            if (usuario.getId().equals(id)) {
                return usuario;
            }
        }

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Usuario no encontrado");
    }

    // Registramos un nuevo cliente
    public UsuarioDto registrar(RegistroDto registro) {

        if (registro == null ||
                registro.getEmail() == null ||
                registro.getEmail().isBlank() ||
                registro.getNombre() == null ||
                registro.getNombre().isBlank() ||
                registro.getPassword() == null ||
                registro.getPassword().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Faltan datos obligatorios");
        }

        String email = registro.getEmail().trim().toLowerCase();

        // Comprobamos que ese correo no exista ya
        for (Usuario usuario : datos.getUsuarios()) {
            if (usuario.getEmail().equalsIgnoreCase(email)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "El email ya existe");
            }
        }

        Usuario usuario = new Usuario(
                datos.nuevoUsuario(),
                email,
                registro.getNombre(),
                registro.getDireccionEnvio(),
                Rol.CLIENTE,
                LocalDateTime.now(),
                passwordEncoder.encode(registro.getPassword())
        );

        datos.getUsuarios().add(usuario);

        return dto(usuario);
    }

    // Comprobamos los datos para iniciar sesion
    public Usuario comprobarLogin(LoginDto login) {

        if (login == null || login.getEmail() == null ||
                login.getPassword() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        }

        for (Usuario usuario : datos.getUsuarios()) {

            if (usuario.getEmail().equalsIgnoreCase(login.getEmail().trim())
                    && passwordEncoder.matches(
                    login.getPassword(), usuario.getPasswordHash())) {

                return usuario;
            }
        }

        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
    }

    // Mostramos los datos de un usuario
    public UsuarioDto detalle(Long id) {
        return dto(buscar(id));
    }

    // Devolvemos la lista de usuarios
    public List<UsuarioDto> listar() {

        List<UsuarioDto> lista = new ArrayList<>();

        for (Usuario usuario : datos.getUsuarios()) {
            lista.add(dto(usuario));
        }

        return lista;
    }

    // Modificamos el nombre y la direccion
    public UsuarioDto modificarPerfil(Long id, PerfilDto perfil) {

        Usuario usuario = buscar(id);

        if (perfil == null || perfil.getNombre() == null ||
                perfil.getNombre().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }

        usuario.setNombre(perfil.getNombre());
        usuario.setDireccionEnvio(perfil.getDireccionEnvio());

        return dto(usuario);
    }

    // Cambiamos el rol de un usuario
    public UsuarioDto cambiarRol(Long id, RolDto datosRol) {

        Usuario usuario = buscar(id);

        if (datosRol == null || datosRol.getRol() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El rol es obligatorio");
        }

        usuario.setRol(datosRol.getRol());

        return dto(usuario);
    }
}
