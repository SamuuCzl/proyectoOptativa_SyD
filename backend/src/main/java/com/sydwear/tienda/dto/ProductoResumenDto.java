package com.sydwear.tienda.dto;

import java.math.BigDecimal;

public class ProductoResumenDto {

    private Long id;
    private String sku;
    private String nombre;
    private BigDecimal precio;
    private int stock;
    private String imagenUrl;
    private Long categoriaId;
    private String talla;
    private String color;
    private boolean activo;
    private boolean destacado;

    public ProductoResumenDto() {
    }

    public ProductoResumenDto(Long id, String sku, String nombre,
                              BigDecimal precio, int stock,
                              String imagenUrl, Long categoriaId,
                              String talla, String color,
                              boolean activo, boolean destacado) {

        this.id = id;
        this.sku = sku;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.imagenUrl = imagenUrl;
        this.categoriaId = categoriaId;
        this.talla = talla;
        this.color = color;
        this.activo = activo;
        this.destacado = destacado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean getDestacado() {
        return destacado;
    }

    public void setDestacado(boolean destacado) {
        this.destacado = destacado;
    }
}