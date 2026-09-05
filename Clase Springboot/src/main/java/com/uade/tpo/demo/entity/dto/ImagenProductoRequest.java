package com.uade.tpo.demo.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ImagenProductoRequest {
    @NotBlank(message = "la url de la imagen es obligatoria")
    private String url;
}
