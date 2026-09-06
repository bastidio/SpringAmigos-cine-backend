package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.entity.Usuario;
import com.uade.tpo.demo.entity.dto.OrdenResponse;
import com.uade.tpo.demo.exceptions.OrdenAccesoDenegadoException;
import com.uade.tpo.demo.exceptions.OrdenNotFoundException;
import com.uade.tpo.demo.service.OrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordenes")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    // GET /ordenes/{id} -> Obtener orden por su ID (solo el dueño o un ADMIN)
    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponse> obtenerPorId(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario)
            throws OrdenAccesoDenegadoException, OrdenNotFoundException {
        OrdenResponse orden = ordenService.getOrdenResponseById(id, usuario);
        return ResponseEntity.ok(orden);
    }

    // GET /ordenes/mis-ordenes -> Historial de ordenes del usuario autenticado
    @GetMapping("/mis-ordenes")
    public ResponseEntity<List<OrdenResponse>> obtenerMisOrdenes(@AuthenticationPrincipal Usuario usuario) {
        List<OrdenResponse> ordenes = ordenService.getOrdenesResponseByUsuarioId(usuario.getId());
        return ResponseEntity.ok(ordenes);
    }

    // GET /ordenes -> Listado de todas las ordenes (panel admin, ver SecurityConfig)
    @GetMapping
    public ResponseEntity<List<OrdenResponse>> obtenerTodas() {
        List<OrdenResponse> ordenes = ordenService.getAllOrdenesResponse();
        return ResponseEntity.ok(ordenes);
    }

    // PUT /ordenes/{id}/cancelar -> Cancelar una orden (solo el dueño o un ADMIN)
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<OrdenResponse> cancelarOrden(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario)
            throws OrdenAccesoDenegadoException, OrdenNotFoundException {
        OrdenResponse ordenCancelada = ordenService.cancelOrden(id, usuario);
        return ResponseEntity.ok(ordenCancelada);
    }
}
