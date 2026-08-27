package com.uade.tpo.demo.entity.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class FuncionRequest {
    private Long peliculaId;
    private Long salaId;
    private Date horario;
    private String idioma;
    private String formato;
    private Float precio_base;
}