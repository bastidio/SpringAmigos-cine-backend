package com.uade.tpo.demo.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PrecioUtils {

    private PrecioUtils() {
    }

    // Único punto donde se aplica el descuento (porcentaje) de un producto sobre su precio.
    public static BigDecimal precioConDescuento(BigDecimal precio, BigDecimal descuento) {
        BigDecimal desc = descuento != null ? descuento : BigDecimal.ZERO;
        BigDecimal factor = BigDecimal.ONE.subtract(desc.divide(BigDecimal.valueOf(100)));
        return precio.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    // Único punto donde se calcula el monto de un código de descuento (porcentaje entero) sobre un subtotal.
    public static BigDecimal montoDescuento(BigDecimal subtotal, Integer porcentaje) {
        return subtotal.multiply(BigDecimal.valueOf(porcentaje))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}