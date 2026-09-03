package com.uade.tpo.demo.util;

public class PrecioUtils {

    private PrecioUtils() {
    }

    // Único punto donde se aplica el descuento de un producto sobre su precio.
    // Aislado para poder migrar a BigDecimal más adelante sin tocar los servicios que lo llaman.
    public static Float precioConDescuento(Float precio, Float descuento) {
        Float descuentoAplicado = descuento != null ? descuento : 0f;
        return precio - (precio * descuentoAplicado / 100f);
    }
}
