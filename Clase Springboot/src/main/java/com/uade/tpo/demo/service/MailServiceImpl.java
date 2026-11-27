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
import com.uade.tpo.demo.entity.dto.NuevaFuncionMail;
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
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final Pattern MARCADOR = Pattern.compile("\\{\\{(\\w+)\\}\\}");

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EntradaRepository entradaRepository;

    @Autowired
    private ItemOrdenRepository itemOrdenRepository;

    @Value("${spring.mail.username:}")
    private String remitente;

    @Value("${app.front.url:http://localhost:5173}")
    private String urlFront;

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

    @Override
    @Async
    public void enviarNuevaFuncionFavorita(NuevaFuncionMail datos) {
        if (remitente == null || remitente.isBlank()) {
            log.info("No se envía el aviso de nueva función a {}: MAIL_USERNAME no está configurado.", datos.email());
            return;
        }

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setFrom(remitente, "Lumière Cine");
            helper.setTo(datos.email());
            helper.setSubject("Hay una nueva función de " + datos.pelicula() + " - Lumière Cine");
            helper.setText(armarHtmlNuevaFuncion(datos), true);
            helper.addInline("logo", new ClassPathResource("mail/lumiere-logotipo.png"), "image/png");

            mailSender.send(mensaje);
            log.info("Aviso de nueva función de {} enviado a {}.", datos.pelicula(), datos.email());
        } catch (Exception e) {
            log.error("No se pudo enviar el aviso de nueva función de {} a {}.", datos.pelicula(), datos.email(), e);
        }
    }

    private String armarHtmlNuevaFuncion(NuevaFuncionMail datos) throws IOException {
        return completar(leerPlantilla("nueva-funcion.html"), Map.of(
                "pelicula", HtmlUtils.htmlEscape(datos.pelicula()),
                "urlFront", urlFront,
                "peliculaId", datos.peliculaId()));
    }

    private byte[] generarQr(String texto) throws WriterException, IOException {
        BitMatrix matriz = new QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, 240, 240);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matriz, "PNG", salida);
        return salida.toByteArray();
    }

    private String armarHtml(Orden orden, List<Entrada> entradas, List<ItemOrden> items) throws IOException {
        // subtotal y montoDescuento solo se guardan cuando hubo codigo de descuento
        BigDecimal subtotal = orden.getSubtotal() != null ? orden.getSubtotal() : orden.getTotal();

        return completar(leerPlantilla("confirmacion.html"), Map.of(
                "numeroOrden", orden.getId(),
                "fecha", orden.getFecha().format(FORMATO_FECHA_HORA),
                "entradas", armarSeccionEntradas(entradas),
                "kiosco", armarSeccionKiosco(items),
                "subtotal", moneda(subtotal),
                "descuento", armarDescuento(orden),
                "total", moneda(orden.getTotal())));
    }

    private String armarSeccionEntradas(List<Entrada> entradas) throws IOException {
        if (entradas.isEmpty()) {
            return "";
        }
        String plantilla = leerPlantilla("entrada.html");
        StringBuilder lista = new StringBuilder();
        for (Entrada entrada : entradas) {
            String butaca = String.valueOf((char) (64 + entrada.getAsiento().getFila())) + entrada.getAsiento().getNumero();
            lista.append(completar(plantilla, Map.of(
                    "butaca", butaca,
                    "numeroEntrada", entrada.getId(),
                    "precio", moneda(entrada.getPrecio()))));
        }

        Funcion funcion = entradas.get(0).getFuncion();
        return completar(leerPlantilla("seccion-entradas.html"), Map.of(
                "pelicula", HtmlUtils.htmlEscape(funcion.getPelicula().getTitulo()),
                "dia", funcion.getHorario().format(FORMATO_FECHA),
                "hora", funcion.getHorario().format(FORMATO_HORA),
                "sala", HtmlUtils.htmlEscape(funcion.getSala().getNombre()),
                "listaEntradas", lista));
    }

    private String armarSeccionKiosco(List<ItemOrden> items) throws IOException {
        if (items.isEmpty()) {
            return "";
        }
        String plantilla = leerPlantilla("producto.html");
        StringBuilder productos = new StringBuilder();
        for (ItemOrden item : items) {
            BigDecimal importe = item.getPrecio_unitario().multiply(BigDecimal.valueOf(item.getCantidad()));
            productos.append(completar(plantilla, Map.of(
                    "cantidad", item.getCantidad(),
                    "nombre", HtmlUtils.htmlEscape(item.getProducto().getNombre()),
                    "importe", moneda(importe))));
        }
        return completar(leerPlantilla("seccion-kiosco.html"), Map.of("productos", productos));
    }

    private String armarDescuento(Orden orden) throws IOException {
        if (orden.getMontoDescuento() == null) {
            return "";
        }
        return completar(leerPlantilla("descuento.html"), Map.of(
                "codigo", HtmlUtils.htmlEscape(orden.getCodigoDescuento()),
                "porcentaje", orden.getPorcentajeDescuento(),
                "monto", moneda(orden.getMontoDescuento())));
    }

    private String leerPlantilla(String nombre) throws IOException {
        try (InputStream entrada = new ClassPathResource("mail/" + nombre).getInputStream()) {
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String completar(String plantilla, Map<String, ?> datos) {
        return MARCADOR.matcher(plantilla).replaceAll(marcador -> {
            Object valor = Objects.requireNonNull(datos.get(marcador.group(1)), "Marcador sin dato: " + marcador.group());
            return Matcher.quoteReplacement(valor.toString());
        });
    }

    private String moneda(BigDecimal monto) {
        NumberFormat formato = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-AR"));
        formato.setMinimumFractionDigits(2);
        formato.setMaximumFractionDigits(2);
        return "$ " + formato.format(monto);
    }
}
