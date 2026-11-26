
package com.sydwear.tienda.controller;

import com.sydwear.tienda.dto.LoginDto;
import com.sydwear.tienda.dto.RegistroDto;
import com.sydwear.tienda.dto.TokenDto;
import com.sydwear.tienda.model.Usuario;
import com.sydwear.tienda.service.JwtService;
import com.sydwear.tienda.service.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthController(UsuarioService usuarioService,
                          JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    // Registramos un cliente nuevo
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenDto registro(@RequestBody RegistroDto datos) {

        usuarioService.registrar(datos);

        // Comprobamos sus datos para generar el token
        LoginDto login = new LoginDto(
                datos.getEmail(),
                datos.getPassword()
        );

        Usuario usuario = usuarioService.comprobarLogin(login);

        return new TokenDto(jwtService.generarToken(usuario));
    }

    // Iniciamos sesion con correo y contraseña
    @PostMapping("/login")
    public TokenDto login(@RequestBody LoginDto datos) {

        Usuario usuario = usuarioService.comprobarLogin(datos);

        return new TokenDto(jwtService.generarToken(usuario));
    }
}
