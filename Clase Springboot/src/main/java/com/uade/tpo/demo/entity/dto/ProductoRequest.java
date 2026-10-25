package com.uade.tpo.demo.entity.dto;
import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ProductoRequest {
    private Long categoriaId;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private BigDecimal descuento;
    private List<String> imagenes;
    // Opcional: si viene null no se toca (al crear queda en true).
    private Boolean activo;
    // URL de la imagen principal: se guarda como primera de "imagenes".
    private String posterProducto;
}
