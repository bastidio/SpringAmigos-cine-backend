package com.uade.tpo.demo.entity.dto;

import lombok.Data;

@Data
public class ItemCarritoRequest {
    private Long productoId;
    private Long asientoId;
    private Integer cantidad;
}
