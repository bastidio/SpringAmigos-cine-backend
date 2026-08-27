package com.uade.tpo.demo.entity.dto;

import lombok.Data;

@Data
public class ProductoRequest {
    private Long categoriaId;
    private String nombre;
    private String descripcion;
    private Float precio;
    private Integer stock;
    private Float descuento;
    private String imagen_url;
}