package com.uade.tpo.demo.entity.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FuncionRequest {
    private Long peliculaId;
    private Long salaId;
    private LocalDateTime horario;
    private String idioma;
    private String formato;
    private BigDecimal precio_base;
}