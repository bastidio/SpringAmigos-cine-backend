package com.uade.tpo.demo.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.uade.tpo.demo.entity.Entrada;
import com.uade.tpo.demo.entity.Funcion;
import com.uade.tpo.demo.entity.ItemOrden;
import com.uade.tpo.demo.entity.Orden;
import com.uade.tpo.demo.repository.EntradaRepository;
import com.uade.tpo.demo.repository.ItemOrdenRepository;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    // Paleta de Lumière (la misma del front). Los estilos van en linea porque los clientes de mail no leen CSS externo.
    private static final String FONDO = "#0b0810";
    private static final String SUPERFICIE = "#16111d";
    private static final String ELEVADO = "#211830";
    private static final String BORDE = "#2a2138";
    private static final String DORADO = "#b8913f";
    private static final String DORADO_TEXTO = "#d9b76a";
    private static final String TEXTO = "#ede6da";
    private static final String TEXTO_SUAVE = "#a89cb0";
    private static final String FUENTE = "Helvetica, Arial, sans-serif";
    private static final String FUENTE_TITULOS = "Georgia, 'Times New Roman', serif";

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EntradaRepository entradaRepository;

    @Autowired
    private ItemOrdenRepository itemOrdenRepository;

    @Value("${spring.mail.username:}")
    private String remitente;

    // Corre en otro hilo para que el checkout no espere a Gmail. El try/catch va aca porque el que llama
    // ya no puede atrapar los errores. Las relaciones que usa el mail son EAGER: no necesitan sesion abierta.
    @Override
    @Async
    public void enviarConfirmacionCompra(Orden orden) {
        if (remitente == null || remitente.isBlank()) {
            log.info("No se envía el mail de la orden {}: MAIL_USERNAME no está configurado.", orden.getId());
            return;
        }

        try {
            enviar(orden);
        } catch (Exception e) {
            log.error("No se pudo enviar el mail de confirmación de la orden {}.", orden.getId(), e);
        }
    }

    private void enviar(Orden orden) throws Exception {
        List<Entrada> entradas = new ArrayList<>(entradaRepository.findByOrden(orden));
        entradas.sort(Comparator.comparing((Entrada e) -> e.getAsiento().getFila())
                .thenComparing(e -> e.getAsiento().getNumero()));
        List<ItemOrden> items = itemOrdenRepository.findByOrdenId(orden.getId());

        // cid -> imagen PNG de cada QR (se adjuntan despues del texto, como pide MimeMessageHelper).
        Map<String, byte[]> qrs = new LinkedHashMap<>();
        for (Entrada entrada : entradas) {
            qrs.put("qr" + entrada.getId(), generarQr("LUMIERE-ENTRADA-" + entrada.getId()));
        }

        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
        helper.setFrom(remitente, "Lumière Cine");
        helper.setTo(orden.getUsuario().getEmail());
        helper.setSubject("Confirmación de tu compra #" + orden.getId() + " - Lumière Cine");
        helper.setText(armarHtml(orden, entradas, items), true);

        helper.addInline("logo", new ClassPathResource("mail/lumiere-logotipo.png"), "image/png");
        for (Map.Entry<String, byte[]> qr : qrs.entrySet()) {
            helper.addInline(qr.getKey(), new ByteArrayResource(qr.getValue()), "image/png");
        }

        mailSender.send(mensaje);
        log.info("Mail de confirmación de la orden {} enviado a {}.", orden.getId(), orden.getUsuario().getEmail());
    }

    private byte[] generarQr(String texto) throws WriterException, IOException {
        BitMatrix matriz = new QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, 240, 240);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matriz, "PNG", salida);
        return salida.toByteArray();
    }

    private String armarHtml(Orden orden, List<Entrada> entradas, List<ItemOrden> items) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\"></head>")
                .append("<body style=\"margin:0;padding:0;background-color:").append(FONDO).append(";\">")
                .append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:")
                .append(FONDO).append(";padding:32px 12px;font-family:").append(FUENTE).append(";color:").append(TEXTO).append(";\">")
                .append("<tr><td align=\"center\">")
                .append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px;background-color:")
                .append(SUPERFICIE).append(";border:1px solid ").append(BORDE).append(";border-radius:16px;\">");

        // Encabezado
        html.append("<tr><td align=\"center\" style=\"padding:36px 32px 8px;\">")
                .append("<img src=\"cid:logo\" alt=\"Lumière Cine\" width=\"200\" style=\"display:block;width:200px;height:auto;border:0;\">")
                .append("</td></tr>")
                .append("<tr><td align=\"center\" style=\"padding:16px 32px 0;\">")
                .append("<p style=\"margin:0;font-size:12px;letter-spacing:3px;color:").append(DORADO_TEXTO).append(";\">COMPRA CONFIRMADA</p>")
                .append("<h1 style=\"margin:12px 0 8px;font-family:").append(FUENTE_TITULOS).append(";font-size:28px;color:").append(TEXTO)
                .append(";\">¡Gracias por tu compra!</h1>")
                .append("<p style=\"margin:0;font-size:14px;color:").append(TEXTO_SUAVE).append(";\">Orden <strong style=\"color:")
                .append(TEXTO).append(";\">#").append(orden.getId()).append("</strong> · ")
                .append(orden.getFecha().format(FORMATO_FECHA_HORA)).append("</p>")
                .append("</td></tr>");

        html.append(separador());

        // Entradas
        if (!entradas.isEmpty()) {
            Funcion funcion = entradas.get(0).getFuncion();
            html.append("<tr><td style=\"padding:0 32px;\">")
                    .append("<p style=\"margin:0 0 4px;font-size:12px;letter-spacing:2px;color:").append(DORADO_TEXTO).append(";\">PELÍCULA</p>")
                    .append("<h2 style=\"margin:0 0 16px;font-family:").append(FUENTE_TITULOS).append(";font-size:22px;color:").append(TEXTO).append(";\">")
                    .append(HtmlUtils.htmlEscape(funcion.getPelicula().getTitulo())).append("</h2>")
                    .append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\"><tr>")
                    .append(dato("Día", funcion.getHorario().format(FORMATO_FECHA)))
                    .append(dato("Hora", funcion.getHorario().format(FORMATO_HORA) + " hs"))
                    .append(dato("Sala", HtmlUtils.htmlEscape(funcion.getSala().getNombre())))
                    .append("</tr></table>")
                    .append("<p style=\"margin:24px 0 12px;font-size:12px;letter-spacing:2px;color:").append(DORADO_TEXTO)
                    .append(";\">TUS ENTRADAS</p>");

            for (Entrada entrada : entradas) {
                String butaca = String.valueOf((char) (64 + entrada.getAsiento().getFila())) + entrada.getAsiento().getNumero();
                html.append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin-bottom:12px;background-color:")
                        .append(ELEVADO).append(";border:1px solid ").append(BORDE).append(";border-radius:12px;\"><tr>")
                        .append("<td style=\"padding:16px 20px;\">")
                        .append("<p style=\"margin:0;font-size:12px;color:").append(TEXTO_SUAVE).append(";\">Butaca</p>")
                        .append("<p style=\"margin:4px 0 0;font-family:").append(FUENTE_TITULOS).append(";font-size:26px;font-weight:bold;color:")
                        .append(DORADO_TEXTO).append(";\">").append(butaca).append("</p>")
                        .append("<p style=\"margin:4px 0 0;font-size:12px;color:").append(TEXTO_SUAVE).append(";\">Entrada #")
                        .append(entrada.getId()).append(" · ").append(moneda(entrada.getPrecio())).append("</p>")
                        .append("</td>")
                        .append("<td align=\"right\" style=\"padding:12px 16px;\">")
                        .append("<img src=\"cid:qr").append(entrada.getId()).append("\" alt=\"QR de la entrada ").append(butaca)
                        .append("\" width=\"110\" height=\"110\" style=\"display:block;border:0;border-radius:8px;background-color:#ffffff;\">")
                        .append("</td></tr></table>");
            }
            html.append("</td></tr>").append(separador());
        }

        // Kiosco
        if (!items.isEmpty()) {
            html.append("<tr><td style=\"padding:0 32px;\">")
                    .append("<p style=\"margin:0 0 12px;font-size:12px;letter-spacing:2px;color:").append(DORADO_TEXTO).append(";\">KIOSCO</p>")
                    .append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\">");
            for (ItemOrden item : items) {
                BigDecimal totalItem = item.getPrecio_unitario().multiply(BigDecimal.valueOf(item.getCantidad()));
                html.append("<tr><td style=\"padding:6px 0;color:").append(TEXTO).append(";\">")
                        .append(item.getCantidad()).append(" × ").append(HtmlUtils.htmlEscape(item.getProducto().getNombre()))
                        .append("</td><td align=\"right\" style=\"padding:6px 0;color:").append(TEXTO).append(";\">")
                        .append(moneda(totalItem)).append("</td></tr>");
            }
            html.append("</table></td></tr>").append(separador());
        }

        // Totales (subtotal y descuento solo se guardan cuando hubo codigo de descuento)
        BigDecimal subtotal = orden.getSubtotal() != null ? orden.getSubtotal() : orden.getTotal();
        html.append("<tr><td style=\"padding:0 32px 32px;\">")
                .append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\">")
                .append(filaTotal("Subtotal", moneda(subtotal)));
        if (orden.getMontoDescuento() != null) {
            html.append(filaTotal("Descuento " + HtmlUtils.htmlEscape(orden.getCodigoDescuento())
                    + " (" + orden.getPorcentajeDescuento() + "%)", "- " + moneda(orden.getMontoDescuento())));
        }
        html.append("<tr><td style=\"padding:12px 0 0;border-top:1px solid ").append(BORDE).append(";font-size:16px;color:").append(TEXTO)
                .append(";\"><strong>Total</strong></td>")
                .append("<td align=\"right\" style=\"padding:12px 0 0;border-top:1px solid ").append(BORDE)
                .append(";font-family:").append(FUENTE_TITULOS).append(";font-size:22px;font-weight:bold;color:").append(DORADO_TEXTO).append(";\">")
                .append(moneda(orden.getTotal())).append("</td></tr>")
                .append("</table></td></tr>");

        html.append("</table>")
                .append("<p style=\"margin:24px 0 0;font-size:12px;color:").append(TEXTO_SUAVE)
                .append(";\">Presentá el QR de cada entrada al ingresar a la sala.</p>")
                .append("</td></tr></table></body></html>");
        return html.toString();
    }

    private String separador() {
        return "<tr><td style=\"padding:24px 32px;\"><div style=\"height:1px;background-color:" + DORADO
                + ";opacity:0.4;font-size:0;line-height:0;\">&nbsp;</div></td></tr>";
    }

    private String dato(String rotulo, String valor) {
        return "<td style=\"padding:0 8px 0 0;vertical-align:top;\">"
                + "<p style=\"margin:0;font-size:12px;color:" + TEXTO_SUAVE + ";\">" + rotulo + "</p>"
                + "<p style=\"margin:4px 0 0;font-size:15px;font-weight:bold;color:" + TEXTO + ";\">" + valor + "</p></td>";
    }

    private String filaTotal(String rotulo, String valor) {
        return "<tr><td style=\"padding:4px 0 8px;color:" + TEXTO_SUAVE + ";\">" + rotulo + "</td>"
                + "<td align=\"right\" style=\"padding:4px 0 8px;color:" + TEXTO + ";\">" + valor + "</td></tr>";
    }

    private String moneda(BigDecimal monto) {
        NumberFormat formato = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-AR"));
        formato.setMinimumFractionDigits(2);
        formato.setMaximumFractionDigits(2);
        return "$ " + formato.format(monto);
    }
}
