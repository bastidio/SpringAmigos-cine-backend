package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Orden;
import com.uade.tpo.demo.service.OrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordenes")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    // GET /ordenes/{id} -> Obtener orden por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Orden> obtenerPorId(@PathVariable Long id) {
        Orden orden = ordenService.getOrdenById(id);
        return ResponseEntity.ok(orden);
    }

    // GET /ordenes/usuario/{usuarioId} -> Historial de órdenes de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Orden>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        List<Orden> ordenes = ordenService.getOrdenesByUsuarioId(usuarioId);
        return ResponseEntity.ok(ordenes);
    }

    // GET /ordenes -> Listado de todas las órdenes (panel admin)
    @GetMapping
    public ResponseEntity<List<Orden>> obtenerTodas() {
        List<Orden> ordenes = ordenService.getAllOrdenes();
        return ResponseEntity.ok(ordenes);
    }

    // PUT /ordenes/{id}/cancelar -> Cancelar una orden
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Orden> cancelarOrden(@PathVariable Long id) {
        Orden ordenCancelada = ordenService.cancelOrden(id);
        return ResponseEntity.ok(ordenCancelada);
    }
}
