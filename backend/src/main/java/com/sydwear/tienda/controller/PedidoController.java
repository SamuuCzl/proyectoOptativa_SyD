
package com.sydwear.tienda.controller;

import com.sydwear.tienda.dto.EstadoPedidoDto;
import com.sydwear.tienda.dto.LineaPedidoDto;
import com.sydwear.tienda.dto.PedidoDto;

import com.sydwear.tienda.model.LineaPedido;
import com.sydwear.tienda.model.Pedido;
import com.sydwear.tienda.service.PedidoService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PedidoController {

    private final PedidoService servicio;

    public PedidoController(PedidoService servicio) {
        this.servicio = servicio;
    }

    // Cogemos el correo del usuario que ha iniciado sesion
    private String emailUsuario() {

        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    // Preparamos los datos del pedido para devolverlos
    private PedidoDto dto(Pedido pedido) {

        List<LineaPedidoDto> lineas = new ArrayList<>();

        for (LineaPedido linea : pedido.getLineas()) {

            lineas.add(new LineaPedidoDto(
                    linea.getProducto().getId(),
                    linea.getNombreProducto(),
                    linea.getPrecioUnitario(),
                    linea.getCantidad(),
                    linea.getSubtotal()
            ));
        }

        return new PedidoDto(
                pedido.getId(),
                pedido.getCodigo(),
                pedido.getFecha(),
                pedido.getEstado(),
                pedido.getDireccionEnvio(),
                pedido.getSubtotal(),
                pedido.getGastosEnvio(),
                pedido.getTotal(),
                lineas
        );
    }

    // Convertimos una lista de pedidos a DTO
    private List<PedidoDto> listaDto(List<Pedido> pedidos) {

        List<PedidoDto> resultado = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            resultado.add(dto(pedido));
        }

        return resultado;
    }

    // Creamos un pedido con los productos del carrito
    @PostMapping("/api/pedidos")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoDto crear() {

        return dto(servicio.crear(emailUsuario()));
    }

    // Consultamos nuestros pedidos
    @GetMapping("/api/pedidos")
    public List<PedidoDto> misPedidos() {

        return listaDto(servicio.misPedidos(emailUsuario()));
    }

    // Consultamos uno de nuestros pedidos
    @GetMapping("/api/pedidos/{id}")
    public PedidoDto miPedido(@PathVariable Long id) {

        return dto(servicio.miPedido(emailUsuario(), id));
    }

    // El administrador consulta todos los pedidos
    @GetMapping("/api/admin/pedidos")
    public List<PedidoDto> listarAdmin() {

        return listaDto(servicio.listar());
    }

    // El administrador consulta un pedido
    @GetMapping("/api/admin/pedidos/{id}")
    public PedidoDto detalleAdmin(@PathVariable Long id) {

        return dto(servicio.detalleAdmin(id));
    }

    // El administrador cambia el estado de un pedido
    @PutMapping("/api/admin/pedidos/{id}/estado")
    public PedidoDto cambiarEstado(
            @PathVariable Long id,
            @RequestBody EstadoPedidoDto datos) {

        if (datos == null || datos.getEstado() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Falta el estado");
        }

        return dto(servicio.cambiarEstado(
                id,
                datos.getEstado()
        ));
    }
}