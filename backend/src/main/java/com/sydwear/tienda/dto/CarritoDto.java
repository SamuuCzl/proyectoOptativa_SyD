
package com.sydwear.tienda.dto;

import java.math.BigDecimal;
import java.util.List;

public class CarritoDto {

    private List<LineaCarritoDto> lineas;
    private BigDecimal total;

    public CarritoDto() {
    }

    public CarritoDto(List<LineaCarritoDto> lineas, BigDecimal total) {
        this.lineas = lineas;
        this.total = total;
    }

    public List<LineaCarritoDto> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaCarritoDto> lineas) {
        this.lineas = lineas;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
