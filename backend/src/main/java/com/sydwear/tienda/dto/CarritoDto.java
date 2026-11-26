
package com.sydwear.tienda.dto;

import java.math.BigDecimal;
import java.util.List;

public class CarritoDto {

    private List<LineaCarritoDto> lineas;
    private int numeroArticulos;
    private BigDecimal subtotal;
    private BigDecimal gastosEnvio;
    private BigDecimal total;
    private BigDecimal faltaEnvioGratis;
    private List<String> avisos;

    public CarritoDto() {
    }

    public CarritoDto(List<LineaCarritoDto> lineas, BigDecimal total) {
        this.lineas = lineas;
        this.total = total;
    }

    public CarritoDto(List<LineaCarritoDto> lineas,
                      int numeroArticulos,
                      BigDecimal subtotal,
                      BigDecimal gastosEnvio,
                      BigDecimal total,
                      BigDecimal faltaEnvioGratis,
                      List<String> avisos) {

        this.lineas = lineas;
        this.numeroArticulos = numeroArticulos;
        this.subtotal = subtotal;
        this.gastosEnvio = gastosEnvio;
        this.total = total;
        this.faltaEnvioGratis = faltaEnvioGratis;
        this.avisos = avisos;
    }

    public List<LineaCarritoDto> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaCarritoDto> lineas) {
        this.lineas = lineas;
    }

    public int getNumeroArticulos() {
        return numeroArticulos;
    }

    public void setNumeroArticulos(int numeroArticulos) {
        this.numeroArticulos = numeroArticulos;
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

    public BigDecimal getFaltaEnvioGratis() {
        return faltaEnvioGratis;
    }

    public void setFaltaEnvioGratis(BigDecimal faltaEnvioGratis) {
        this.faltaEnvioGratis = faltaEnvioGratis;
    }

    public List<String> getAvisos() {
        return avisos;
    }

    public void setAvisos(List<String> avisos) {
        this.avisos = avisos;
    }
}