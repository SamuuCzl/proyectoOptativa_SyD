
package com.sydwear.tienda.service;

import com.sydwear.tienda.model.Carrito;
import com.sydwear.tienda.model.LineaCarrito;
import com.sydwear.tienda.model.Producto;
import com.sydwear.tienda.model.Usuario;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarritoService {

    private final CatalogoDatosService datos;
    private final List<Carrito> carritos = new ArrayList<>();

    public CarritoService(CatalogoDatosService datos) {
        this.datos = datos;
    }

    // Buscamos al usuario por su correo
    private Usuario buscarUsuario(String email) {

        for (Usuario usuario : datos.getUsuarios()) {
            if (usuario.getEmail().equalsIgnoreCase(email)) {
                return usuario;
            }
        }

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Usuario no encontrado");
    }

    // Buscamos un producto por su id
    private Producto buscarProducto(Long id) {

        for (Producto producto : datos.getProductos()) {
            if (producto.getId().equals(id)) {
                return producto;
            }
        }

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Producto no encontrado");
    }

    // Buscamos el carrito del usuario o creamos uno nuevo
    public Carrito obtenerCarrito(String email) {

        Usuario usuario = buscarUsuario(email);

        for (Carrito carrito : carritos) {
            if (carrito.getUsuario().getId().equals(usuario.getId())) {
                return carrito;
            }
        }

        Carrito carrito = new Carrito(usuario, new ArrayList<>());
        carritos.add(carrito);

        return carrito;
    }

    // Añadimos un producto al carrito
    public Carrito agregar(String email, Long idProducto, int cantidad) {

        if (cantidad <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cantidad incorrecta");
        }

        Producto producto = buscarProducto(idProducto);
        Carrito carrito = obtenerCarrito(email);

        // Si ya tenemos el producto, aumentamos su cantidad
        for (LineaCarrito linea : carrito.getLineas()) {

            if (linea.getProducto().getId().equals(idProducto)) {

                if (cantidad > producto.getStock() - linea.getCantidad()) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "No hay suficiente stock");
                }

                linea.setCantidad(linea.getCantidad() + cantidad);
                return carrito;
            }
        }

        if (cantidad > producto.getStock()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "No hay suficiente stock");
        }

        carrito.getLineas().add(new LineaCarrito(producto, cantidad));

        return carrito;
    }

    // Cambiamos la cantidad de un producto
    public Carrito modificar(String email, Long idProducto, int cantidad) {

        if (cantidad <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cantidad incorrecta");
        }

        Carrito carrito = obtenerCarrito(email);

        for (LineaCarrito linea : carrito.getLineas()) {

            if (linea.getProducto().getId().equals(idProducto)) {

                if (cantidad > linea.getProducto().getStock()) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "No hay suficiente stock");
                }

                linea.setCantidad(cantidad);
                return carrito;
            }
        }

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Producto no esta en el carrito");
    }

    // Quitamos un producto del carrito
    public Carrito eliminar(String email, Long idProducto) {

        Carrito carrito = obtenerCarrito(email);

        for (int i = 0; i < carrito.getLineas().size(); i++) {

            LineaCarrito linea = carrito.getLineas().get(i);

            if (linea.getProducto().getId().equals(idProducto)) {
                carrito.getLineas().remove(i);
                return carrito;
            }
        }

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Producto no esta en el carrito");
    }

    // Vaciamos el carrito
    public Carrito vaciar(String email) {

        Carrito carrito = obtenerCarrito(email);
        carrito.getLineas().clear();

        return carrito;
    }

    // Calculamos el precio total del carrito
    public BigDecimal total(String email) {

        Carrito carrito = obtenerCarrito(email);
        BigDecimal total = BigDecimal.ZERO;

        for (LineaCarrito linea : carrito.getLineas()) {

            BigDecimal subtotal = linea.getProducto().getPrecio()
                    .multiply(BigDecimal.valueOf(linea.getCantidad()));

            total = total.add(subtotal);
        }

        return total;
    }
}
