package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.ItemCarrito;
import com.uade.tpo.demo.repository.ItemCarritoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LimpiezaReservasScheduler {

    private final ItemCarritoRepository itemCarritoRepository;

    @Scheduled(fixedRate = 60000)
    public void limpiarReservasVencidas() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(ItemCarrito.MINUTOS_RESERVA);
        List<ItemCarrito> vencidos = itemCarritoRepository.findByAsientoIsNotNullAndReservadoEnBefore(limite);
        itemCarritoRepository.deleteAll(vencidos);
    }
}
