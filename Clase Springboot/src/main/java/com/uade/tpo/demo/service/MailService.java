package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Orden;

public interface MailService {

    // Manda al usuario de la orden el mail con el detalle de la compra y un QR por entrada.
    // Es asincrono y no lanza errores: si el envio falla solo se registra en el log.
    void enviarConfirmacionCompra(Orden orden);
}
