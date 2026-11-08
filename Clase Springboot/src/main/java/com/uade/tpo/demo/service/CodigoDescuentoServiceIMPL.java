package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.CodigoDescuento;
import com.uade.tpo.demo.entity.dto.DescuentoValidacionResponse;
import com.uade.tpo.demo.exceptions.CodigoDescuentoDuplicateException;
import com.uade.tpo.demo.exceptions.CodigoDescuentoInvalidoException;
import com.uade.tpo.demo.exceptions.CodigoDescuentoNotFoundException;
import com.uade.tpo.demo.exceptions.ValidacionException;
import com.uade.tpo.demo.repository.CodigoDescuentoRepository;
import com.uade.tpo.demo.util.PrecioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CodigoDescuentoServiceIMPL implements CodigoDescuentoService {

    @Autowired
    private CodigoDescuentoRepository codigoDescuentoRepository;

    @Override
    public List<CodigoDescuento> getCodigos() {
        return codigoDescuentoRepository.findAll();
    }

    @Override
    public CodigoDescuento createCodigo(String codigo, Integer porcentaje, LocalDate fechaVencimiento)
            throws CodigoDescuentoDuplicateException {
        String normalizado = normalizar(codigo);
        Map<String, String> errores = validarPorcentaje(porcentaje);
        if (normalizado.isEmpty()) {
            errores.put("codigo", "el codigo es obligatorio");
        }
        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }
        // Cuenta tambien si esta inactivo: el admin lo tiene que reactivar, no crear otro.
        if (codigoDescuentoRepository.findByCodigo(normalizado).isPresent()) {
            throw new CodigoDescuentoDuplicateException();
        }

        CodigoDescuento nuevo = new CodigoDescuento();
        nuevo.setCodigo(normalizado);
        nuevo.setPorcentaje(porcentaje);
        nuevo.setFechaVencimiento(fechaVencimiento);
        return codigoDescuentoRepository.save(nuevo);
    }

    @Override
    public CodigoDescuento updateCodigo(Long id, Integer porcentaje, LocalDate fechaVencimiento)
            throws CodigoDescuentoNotFoundException {
        Map<String, String> errores = validarPorcentaje(porcentaje);
        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }
        CodigoDescuento codigo = codigoDescuentoRepository.findById(id)
                .orElseThrow(CodigoDescuentoNotFoundException::new);
        codigo.setPorcentaje(porcentaje);
        codigo.setFechaVencimiento(fechaVencimiento);
        return codigoDescuentoRepository.save(codigo);
    }

    @Override
    public void deleteCodigo(Long id) throws CodigoDescuentoNotFoundException {
        CodigoDescuento codigo = codigoDescuentoRepository.findById(id)
                .orElseThrow(CodigoDescuentoNotFoundException::new);
        codigo.setActivo(false);
        codigoDescuentoRepository.save(codigo);
    }

    @Override
    public CodigoDescuento reactivarCodigo(Long id) throws CodigoDescuentoNotFoundException {
        CodigoDescuento codigo = codigoDescuentoRepository.findById(id)
                .orElseThrow(CodigoDescuentoNotFoundException::new);
        codigo.setActivo(true);
        return codigoDescuentoRepository.save(codigo);
    }

    @Override
    public CodigoDescuento obtenerValido(String codigo) throws CodigoDescuentoInvalidoException {
        return codigoDescuentoRepository.findByCodigo(normalizar(codigo))
                .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                // Vale hasta el fin del dia de vencimiento.
                .filter(c -> c.getFechaVencimiento() == null || !LocalDate.now().isAfter(c.getFechaVencimiento()))
                .orElseThrow(CodigoDescuentoInvalidoException::new);
    }

    @Override
    public DescuentoValidacionResponse validar(String codigo, BigDecimal subtotal) {
        try {
            CodigoDescuento valido = obtenerValido(codigo);
            BigDecimal base = subtotal.setScale(2, RoundingMode.HALF_UP);
            BigDecimal monto = PrecioUtils.montoDescuento(base, valido.getPorcentaje());
            return new DescuentoValidacionResponse(true, null, valido.getCodigo(), valido.getPorcentaje(),
                    base, monto, base.subtract(monto));
        } catch (CodigoDescuentoInvalidoException e) {
            return new DescuentoValidacionResponse(false,
                    "El código no existe, está inactivo o venció", null, null, null, null, null);
        }
    }

    // Mayusculas y sin espacios, para guardar y para buscar.
    private String normalizar(String codigo) {
        return codigo == null ? "" : codigo.replaceAll("\\s+", "").toUpperCase();
    }

    private Map<String, String> validarPorcentaje(Integer porcentaje) {
        Map<String, String> errores = new LinkedHashMap<>();
        if (porcentaje == null) {
            errores.put("porcentaje", "el porcentaje es obligatorio");
        } else if (porcentaje < 1 || porcentaje > 100) {
            errores.put("porcentaje", "el porcentaje debe estar entre 1 y 100");
        }
        return errores;
    }
}
