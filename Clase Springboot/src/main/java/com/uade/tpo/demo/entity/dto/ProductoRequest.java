package com.uade.tpo.demo.entity.dto;
import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ProductoRequest {
    @NotNull(message = "la categoria es obligatoria")
    private Long categoriaId;

    @NotBlank(message = "el nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "el precio es obligatorio")
    @Positive(message = "el precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotNull(message = "el stock es obligatorio")
    @PositiveOrZero(message = "el stock no puede ser negativo")
    private Integer stock;

    @DecimalMin(value = "0.0", message = "el descuento no puede ser negativo")
    @DecimalMax(value = "100.0", message = "el descuento no puede superar 100")
    private BigDecimal descuento;

    private List<String> imagenes;
}
