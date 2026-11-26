
package com.sydwear.tienda.service;

import com.sydwear.tienda.model.Carrito;
import com.sydwear.tienda.model.EstadoPedido;
import com.sydwear.tienda.model.LineaCarrito;
import com.sydwear.tienda.model.LineaPedido;
import com.sydwear.tienda.model.Pedido;
import com.sydwear.tienda.model.Producto;
import com.sydwear.tienda.model.Usuario;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final CatalogoDatosService datos;
    private final CarritoService carritoService;

    private final List<Pedido> pedidos = new ArrayList<>();
    private long siguientePedido = 1;

    public PedidoService(CatalogoDatosService datos,
                         CarritoService carritoService) {
        this.datos = datos;
        this.carritoService = carritoService;
    }

    // Buscamos el usuario que esta comprando
    private Usuario buscarUsuario(String email) {

        for (Usuario usuario : datos.getUsuarios()) {
            if (usuario.getEmail().equalsIgnoreCase(email)) {
                return usuario;
            }
        }

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Usuario no encontrado");
    }

    // Buscamos un pedido por su id
    private Pedido buscarPedido(Long id) {

        for (Pedido pedido : pedidos) {
            if (pedido.getId().equals(id)) {
                return pedido;
            }
        }

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Pedido no encontrado");
    }

    // Creamos un pedido con los productos del carrito
    public Pedido crear(String email) {

        Usuario usuario = buscarUsuario(email);
        Carrito carrito = carritoService.obtenerCarrito(email);

        // No dejamos comprar si el carrito esta vacio
        if (carrito.getLineas().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El carrito esta vacio");
        }

        // Comprobamos que tiene direccion de envio
        if (usuario.getDireccionEnvio() == null ||
                usuario.getDireccionEnvio().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Falta la direccion de envio");
        }

        // Revisamos si hay problemas con los productos
        List<String> avisos = carritoService.avisos(email);

        if (!avisos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    String.join(", ", avisos));
        }

        // Comprobamos todas las cantidades antes de comprar
        for (LineaCarrito linea : carrito.getLineas()) {

            if (linea.getCantidad() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Cantidad incorrecta");
            }

            if (!linea.getProducto().getActivo()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Producto retirado: " +
                                linea.getProducto().getNombre());
            }

            if (linea.getCantidad() > linea.getProducto().getStock()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Stock insuficiente: " +
                                linea.getProducto().getNombre());
            }
        }

        List<LineaPedido> lineasPedido = new ArrayList<>();

        // Guardamos los productos con el precio del momento
        for (LineaCarrito linea : carrito.getLineas()) {

            Producto producto = linea.getProducto();
            int cantidad = linea.getCantidad();

            BigDecimal precio = producto.getPrecio();

            BigDecimal totalLinea = precio.multiply(
                    BigDecimal.valueOf(cantidad)
            );

            LineaPedido lineaPedido = new LineaPedido(
                    producto,
                    producto.getNombre(),
                    precio,
                    cantidad,
                    totalLinea
            );

            lineasPedido.add(lineaPedido);
        }

        // Calculamos los importes igual que en el carrito
        BigDecimal subtotal = carritoService.total(email);
        BigDecimal gastosEnvio = carritoService.gastosEnvio(email);
        BigDecimal total = subtotal.add(gastosEnvio);

        Long id = siguientePedido++;

        Pedido pedido = new Pedido(
                id,
                "PED-" + id,
                usuario,
                LocalDateTime.now(),
                EstadoPedido.PENDIENTE,
                usuario.getDireccionEnvio(),
                subtotal,
                gastosEnvio,
                total,
                lineasPedido
        );

        // Descontamos las unidades compradas
        for (LineaCarrito linea : carrito.getLineas()) {

            Producto producto = linea.getProducto();

            producto.setStock(
                    producto.getStock() - linea.getCantidad()
            );
        }

        pedidos.add(pedido);

        // Vaciamos el carrito despues de comprar
        carritoService.vaciar(email);

        return pedido;
    }

    // Mostramos los pedidos del usuario
    public List<Pedido> misPedidos(String email) {

        List<Pedido> resultado = new ArrayList<>();

        for (Pedido pedido : pedidos) {

            if (pedido.getUsuario().getEmail().equalsIgnoreCase(email)) {
                resultado.add(pedido);
            }
        }

        return resultado;
    }

    // Consultamos uno de nuestros pedidos
    public Pedido miPedido(String email, Long id) {

        Pedido pedido = buscarPedido(id);

        if (!pedido.getUsuario().getEmail().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Pedido no encontrado");
        }

        return pedido;
    }

    // El cliente cancela uno de sus pedidos
    public Pedido cancelarMiPedido(String email, Long id) {

        // Primero comprobamos que el pedido es suyo
        Pedido pedido = miPedido(email, id);

        // Utilizamos el mismo cambio de estado que el administrador
        return cambiarEstado(pedido.getId(), EstadoPedido.CANCELADO);
    }

    // Devolvemos todos los pedidos al administrador
    public List<Pedido> listar() {
        return pedidos;
    }

    // Buscamos cualquier pedido para administracion
    public Pedido detalleAdmin(Long id) {
        return buscarPedido(id);
    }

    // Cambiamos el estado de un pedido
    public Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado) {

        Pedido pedido = buscarPedido(id);

        if (nuevoEstado == null ||
                !pedido.getEstado().puedeCambiarA(nuevoEstado)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cambio de estado no permitido");
        }

        // Si cancelamos, devolvemos el stock al catalogo
        if (nuevoEstado == EstadoPedido.CANCELADO) {

            for (LineaPedido linea : pedido.getLineas()) {

                Producto producto = linea.getProducto();

                producto.setStock(
                        producto.getStock() + linea.getCantidad()
                );
            }
        }

        pedido.setEstado(nuevoEstado);

        return pedido;
    }
}