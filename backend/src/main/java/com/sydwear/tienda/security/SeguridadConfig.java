
package com.sydwear.tienda.security;

import com.sydwear.tienda.service.CatalogoDatosService;
import com.sydwear.tienda.service.JwtService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SeguridadConfig {

    @Bean
    public SecurityFilterChain seguridad(
            HttpSecurity http,
            JwtService jwtService,
            CatalogoDatosService datos) throws Exception {

        JwtFiltro filtro = new JwtFiltro(jwtService, datos);

        // Desactivamos CSRF porque usamos tokens JWT
        http.csrf(csrf -> csrf.disable());

        // No guardamos sesiones de usuarios
        http.sessionManagement(sesion ->
                sesion.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                )
        );

        // Indicamos las rutas publicas y las protegidas
        http.authorizeHttpRequests(permisos -> permisos

                // Dejamos que Spring muestre los errores
                .requestMatchers("/error").permitAll()

                // Registro y login publicos
                .requestMatchers("/api/auth/**").permitAll()

                // Solo los administradores pueden acceder
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Cualquier persona puede consultar el catalogo
                .requestMatchers(HttpMethod.GET,
                        "/api/productos",
                        "/api/productos/**",
                        "/api/categorias",
                        "/api/categorias/**"
                ).permitAll()

                // El resto necesita iniciar sesion
                .anyRequest().authenticated()
        );

        // Comprobamos el token antes de autorizar
        http.addFilterBefore(
                filtro,
                UsernamePasswordAuthenticationFilter.class
        );

        // Respondemos con 401 o 403 segun el problema
        http.exceptionHandling(errores -> errores

                .authenticationEntryPoint((request, response, error) ->
                        response.sendError(401, "No autenticado")
                )

                .accessDeniedHandler((request, response, error) ->
                        response.sendError(403, "Acceso prohibido")
                )
        );

        return http.build();
    }
}