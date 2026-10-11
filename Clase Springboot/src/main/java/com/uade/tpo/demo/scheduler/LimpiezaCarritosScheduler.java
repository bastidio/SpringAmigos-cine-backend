package com.uade.tpo.demo.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.uade.tpo.demo.entity.Carrito;
import com.uade.tpo.demo.repository.CarritoRepository;
import com.uade.tpo.demo.service.CarritoService;

// Corre cada 30 segundos: busca los carritos cuyo timer de 5 minutos ya vencio y los vacia.
@Component
public class LimpiezaCarritosScheduler {

    // Margen despues del vencimiento antes de limpiar.
    private static final long MARGEN_SEGUNDOS = 30;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoService carritoService;

    @Scheduled(fixedRate = 30000)
    public void limpiarCarritosVencidos() {
        LocalDateTime limite = LocalDateTime.now().minusSeconds(MARGEN_SEGUNDOS);
        List<Carrito> vencidos = carritoRepository.findAllByExpiraEnBefore(limite);

        for (Carrito carrito : vencidos) {
            try {
                // Cada carrito en su propia transaccion, re-chequeando el vencimiento adentro.
                carritoService.vaciarSiVencido(carrito.getId(), limite);
            } catch (RuntimeException e) {
            }
        }
    }
}
