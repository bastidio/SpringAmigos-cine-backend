package com.uade.tpo.demo.service;


import com.uade.tpo.demo.entity.Orden;
import java.util.List;

public interface OrdenService {
    Orden getOrdenById(Long id);
    List<Orden> getOrdenesByUsuarioId(Long usuarioId);
    List<Orden> getAllOrdenes();
    Orden cancelOrden(Long id);

}
