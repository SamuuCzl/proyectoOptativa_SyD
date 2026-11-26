
package com.sydwear.tienda.dto;

import com.sydwear.tienda.model.EstadoPedido;

public class EstadoPedidoDto {

    private EstadoPedido estado;

    public EstadoPedidoDto() {
    }

    public EstadoPedidoDto(EstadoPedido estado) {
        this.estado = estado;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
}