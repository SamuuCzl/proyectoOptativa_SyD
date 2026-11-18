package com.sydwear.tienda.model;

import java.math.BigDecimal;

public class LineaPedido {

    private Producto producto;
    private String nombreProducto;
    private BigDecimal precioUnitario;
    private int cantidad;
    private BigDecimal subtotal;

    public LineaPedido() {
    }

    public LineaPedido(Producto producto, String nombreProducto,
                       BigDecimal precioUnitario, int cantidad,
                       BigDecimal subtotal) {

        this.producto = producto;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
