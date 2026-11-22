
package com.sydwear.tienda.dto;

import com.sydwear.tienda.model.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoDto {

    private Long id;
    private String codigo;
    private LocalDateTime fecha;
    private EstadoPedido estado;
    private String direccionEnvio;
    private BigDecimal subtotal;
    private BigDecimal gastosEnvio;
    private BigDecimal total;
    private List<LineaPedidoDto> lineas;

    public PedidoDto() {
    }

    public PedidoDto(Long id, String codigo, LocalDateTime fecha,
                     EstadoPedido estado, String direccionEnvio,
                     BigDecimal subtotal, BigDecimal gastosEnvio,
                     BigDecimal total, List<LineaPedidoDto> lineas) {
        this.id = id;
        this.codigo = codigo;
        this.fecha = fecha;
        this.estado = estado;
        this.direccionEnvio = direccionEnvio;
        this.subtotal = subtotal;
        this.gastosEnvio = gastosEnvio;
        this.total = total;
        this.lineas = lineas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getGastosEnvio() {
        return gastosEnvio;
    }

    public void setGastosEnvio(BigDecimal gastosEnvio) {
        this.gastosEnvio = gastosEnvio;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<LineaPedidoDto> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaPedidoDto> lineas) {
        this.lineas = lineas;
    }
}