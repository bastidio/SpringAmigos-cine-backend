package com.uade.tpo.demo.entity.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CodigoDescuentoRequest {
    private String codigo;
    private Integer porcentaje;
    private LocalDate fechaVencimiento;
}
