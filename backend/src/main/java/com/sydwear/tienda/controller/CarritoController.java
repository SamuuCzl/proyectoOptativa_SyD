
package com.sydwear.tienda.controller;

import com.sydwear.tienda.dto.CarritoAgregarDto;
import com.sydwear.tienda.dto.CarritoCantidadDto;
import com.sydwear.tienda.dto.CarritoDto;
import com.sydwear.tienda.dto.LineaCarritoDto;

import com.sydwear.tienda.model.Carrito;
import com.sydwear.tienda.model.LineaCarrito;
import com.sydwear.tienda.service.CarritoService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService servicio;

    public CarritoController(CarritoService servicio) {
        this.servicio = servicio;
    }

    // Cogemos el correo del usuario que ha iniciado sesion
    private String emailUsuario() {

        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    // Preparamos los datos del carrito para devolverlos
    private CarritoDto dto(Carrito carrito) {

        List<LineaCarritoDto> lineas = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (LineaCarrito linea : carrito.getLineas()) {

            BigDecimal precio = linea.getProducto().getPrecio();

            BigDecimal subtotal = precio.multiply(
                    BigDecimal.valueOf(linea.getCantidad())
            );

            lineas.add(new LineaCarritoDto(
                    linea.getProducto().getId(),
                    linea.getProducto().getNombre(),
                    linea.getCantidad(),
                    precio,
                    subtotal
            ));

            total = total.add(subtotal);
        }

        return new CarritoDto(lineas, total);
    }

    // Mostramos nuestro carrito
    @GetMapping
    public CarritoDto verCarrito() {

        Carrito carrito = servicio.obtenerCarrito(emailUsuario());

        return dto(carrito);
    }

    // Añadimos un producto al carrito
    @PostMapping("/lineas")
    @ResponseStatus(HttpStatus.CREATED)
    public CarritoDto agregar(@RequestBody CarritoAgregarDto datos) {

        if (datos == null || datos.getProductoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Falta el producto");
        }

        Carrito carrito = servicio.agregar(
                emailUsuario(),
                datos.getProductoId(),
                datos.getCantidad()
        );

        return dto(carrito);
    }

    // Cambiamos la cantidad de un producto
    @PutMapping("/lineas/{id}")
    public CarritoDto modificar(
            @PathVariable Long id,
            @RequestBody CarritoCantidadDto datos) {

        if (datos == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Falta la cantidad");
        }

        Carrito carrito = servicio.modificar(
                emailUsuario(),
                id,
                datos.getCantidad()
        );

        return dto(carrito);
    }

    // Quitamos un producto del carrito
    @DeleteMapping("/lineas/{id}")
    public CarritoDto eliminar(@PathVariable Long id) {

        Carrito carrito = servicio.eliminar(emailUsuario(), id);

        return dto(carrito);
    }

    // Vaciamos el carrito completo
    @DeleteMapping
    public CarritoDto vaciar() {

        Carrito carrito = servicio.vaciar(emailUsuario());

        return dto(carrito);
    }
}
