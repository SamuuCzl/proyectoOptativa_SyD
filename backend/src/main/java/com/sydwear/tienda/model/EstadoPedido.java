package com.sydwear.tienda.model;

public enum EstadoPedido {

    PENDIENTE,
    PAGADO,
    ENVIADO,
    ENTREGADO,
    CANCELADO;

    // Comprobamos si podemos cambiar el estado del pedido
    public boolean puedeCambiarA(EstadoPedido nuevo) {

        if (this == PENDIENTE) {
            return nuevo == PAGADO || nuevo == CANCELADO;
        }

        if (this == PAGADO) {
            return nuevo == ENVIADO || nuevo == CANCELADO;
        }

        if (this == ENVIADO) {
            return nuevo == ENTREGADO;
        }

        return false;
    }
}
