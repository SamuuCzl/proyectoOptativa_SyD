
package com.sydwear.tienda.service;

import com.sydwear.tienda.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // Creamos una clave para firmar nuestros tokens
    private final SecretKey clave = Jwts.SIG.HS256.key().build();

    // Creamos un token para el usuario que inicia sesion
    public String generarToken(Usuario usuario) {

        Date ahora = new Date();
        Date caducidad = new Date(ahora.getTime() + 3600000);

        return Jwts.builder()
                .subject(usuario.getEmail())
                .issuedAt(ahora)
                .expiration(caducidad)
                .signWith(clave)
                .compact();
    }

    // Comprobamos el token y obtenemos su correo
    public String obtenerEmail(String token) {

        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
