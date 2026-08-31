package com.uade.tpo.demo.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntradaRequest {
    private Long funcionId;
    private Long asientoId;
}
