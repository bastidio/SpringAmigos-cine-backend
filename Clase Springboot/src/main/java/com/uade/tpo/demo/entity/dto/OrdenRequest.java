package com.uade.tpo.demo.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenRequest {
    private Long usuarioId;
    private List<ItemOrdenRequest> items;
}
