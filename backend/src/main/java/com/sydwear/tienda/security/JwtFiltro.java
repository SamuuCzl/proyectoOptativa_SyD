
package com.sydwear.tienda.security;

import com.sydwear.tienda.model.Usuario;
import com.sydwear.tienda.service.CatalogoDatosService;
import com.sydwear.tienda.service.JwtService;

import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtFiltro extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CatalogoDatosService datos;

    public JwtFiltro(JwtService jwtService, CatalogoDatosService datos) {
        this.jwtService = jwtService;
        this.datos = datos;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String cabecera = request.getHeader("Authorization");

        // Si no hay token, dejamos que Spring compruebe si hace falta
        if (cabecera == null || !cabecera.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = cabecera.substring(7);

        try {
            // Comprobamos el token y buscamos al usuario
            String email = jwtService.obtenerEmail(token);

            for (Usuario usuario : datos.getUsuarios()) {

                if (usuario.getEmail().equalsIgnoreCase(email)) {

                    String rol = "ROLE_" + usuario.getRol().name();

                    var permisos = List.of(
                            new SimpleGrantedAuthority(rol)
                    );

                    var autenticacion =
                            new UsernamePasswordAuthenticationToken(
                                    usuario.getEmail(),
                                    null,
                                    permisos
                            );

                    SecurityContextHolder.getContext()
                            .setAuthentication(autenticacion);

                    break;
                }
            }

        } catch (JwtException | IllegalArgumentException error) {

            // Si el token es incorrecto o ha caducado
            response.sendError(401, "Token incorrecto o caducado");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
