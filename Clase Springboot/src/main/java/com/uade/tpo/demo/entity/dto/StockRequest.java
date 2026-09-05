package com.uade.tpo.demo.entity.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class StockRequest {
    @NotNull(message = "el stock es obligatorio")
    @PositiveOrZero(message = "el stock no puede ser negativo")
    private Integer stock;
}
