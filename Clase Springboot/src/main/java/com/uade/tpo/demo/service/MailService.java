package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Orden;
import com.uade.tpo.demo.entity.dto.NuevaFuncionMail;

public interface MailService {

    // Manda al usuario de la orden el mail con el detalle de la compra y un QR por entrada.
    // Es asincrono y no lanza errores: si el envio falla solo se registra en el log.
    void enviarConfirmacionCompra(Orden orden);

    // Avisa que hay una nueva funcion de una pelicula favorita. Tambien es asincrono y no lanza errores.
    void enviarNuevaFuncionFavorita(NuevaFuncionMail datos);
}
