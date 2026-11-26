
package com.sydwear.tienda.dto;

import java.math.BigDecimal;

public class LineaCarritoDto {

    private Long productoId;
    private String nombre;
    private int cantidad;
    private BigDecimal precio;
    private BigDecimal subtotal;

    public LineaCarritoDto() {
    }

    public LineaCarritoDto(Long productoId, String nombre,
                           int cantidad, BigDecimal precio,
                           BigDecimal subtotal) {

        this.productoId = productoId;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.subtotal = subtotal;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
