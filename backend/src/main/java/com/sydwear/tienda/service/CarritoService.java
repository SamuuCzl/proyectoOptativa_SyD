
package com.sydwear.tienda.service;

import com.sydwear.tienda.model.Carrito;
import com.sydwear.tienda.model.LineaCarrito;
import com.sydwear.tienda.model.Producto;
import com.sydwear.tienda.model.Usuario;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${tienda.envio.coste:4.99}")
    private BigDecimal costeEnvio;

    @Value("${tienda.envio.gratis-desde:60.00}")
    private BigDecimal envioGratisDesde;

    @Value("${tienda.carrito.max-unidades-por-producto:10}")
    private int maxUnidades;

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

    // Comprobamos la cantidad y el estado del producto
    private void comprobarProducto(Producto producto, int cantidad) {

        if (cantidad <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cantidad incorrecta");
        }

        if (!producto.getActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Producto retirado");
        }

        if (cantidad > maxUnidades) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Supera el maximo de unidades por producto");
        }

        if (cantidad > producto.getStock()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "No hay suficiente stock");
        }
    }

    // Añadimos un producto al carrito
    public Carrito agregar(String email, Long idProducto, int cantidad) {

        Producto producto = buscarProducto(idProducto);
        Carrito carrito = obtenerCarrito(email);

        if (cantidad <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cantidad incorrecta");
        }

        // Si ya existe, sumamos la cantidad
        for (LineaCarrito linea : carrito.getLineas()) {

            if (linea.getProducto().getId().equals(idProducto)) {

                int nuevaCantidad = linea.getCantidad() + cantidad;

                comprobarProducto(producto, nuevaCantidad);

                linea.setCantidad(nuevaCantidad);
                return carrito;
            }
        }

        comprobarProducto(producto, cantidad);

        carrito.getLineas().add(new LineaCarrito(producto, cantidad));

        return carrito;
    }

    // Cambiamos la cantidad de un producto
    public Carrito modificar(String email, Long idProducto, int cantidad) {

        Carrito carrito = obtenerCarrito(email);

        for (LineaCarrito linea : carrito.getLineas()) {

            if (linea.getProducto().getId().equals(idProducto)) {

                comprobarProducto(linea.getProducto(), cantidad);

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

    // Vaciamos el carrito completo
    public Carrito vaciar(String email) {

        Carrito carrito = obtenerCarrito(email);
        carrito.getLineas().clear();

        return carrito;
    }

    // Calculamos el subtotal de los productos
    public BigDecimal total(String email) {

        Carrito carrito = obtenerCarrito(email);
        BigDecimal subtotal = BigDecimal.ZERO;

        for (LineaCarrito linea : carrito.getLineas()) {

            BigDecimal precioLinea = linea.getProducto().getPrecio()
                    .multiply(BigDecimal.valueOf(linea.getCantidad()));

            subtotal = subtotal.add(precioLinea);
        }

        return subtotal;
    }

    // Contamos todas las unidades del carrito
    public int numeroArticulos(String email) {

        Carrito carrito = obtenerCarrito(email);
        int cantidad = 0;

        for (LineaCarrito linea : carrito.getLineas()) {
            cantidad = cantidad + linea.getCantidad();
        }

        return cantidad;
    }

    // Calculamos los gastos de envio
    public BigDecimal gastosEnvio(String email) {

        BigDecimal subtotal = total(email);

        if (subtotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        if (subtotal.compareTo(envioGratisDesde) >= 0) {
            return BigDecimal.ZERO;
        }

        return costeEnvio;
    }

    // Calculamos cuanto falta para el envio gratis
    public BigDecimal faltaEnvioGratis(String email) {

        BigDecimal subtotal = total(email);

        if (subtotal.compareTo(envioGratisDesde) >= 0) {
            return BigDecimal.ZERO;
        }

        return envioGratisDesde.subtract(subtotal);
    }

    // Avisamos si un producto esta retirado o no tiene stock
    public List<String> avisos(String email) {

        Carrito carrito = obtenerCarrito(email);
        List<String> avisos = new ArrayList<>();

        for (LineaCarrito linea : carrito.getLineas()) {

            Producto producto = linea.getProducto();

            if (!producto.getActivo()) {
                avisos.add("Producto retirado: " + producto.getNombre());
            }

            if (linea.getCantidad() > producto.getStock()) {
                avisos.add("Stock insuficiente: " + producto.getNombre());
            }

            if (linea.getCantidad() > maxUnidades) {
                avisos.add("Demasiadas unidades: " + producto.getNombre());
            }
        }

        return avisos;
    }
}
