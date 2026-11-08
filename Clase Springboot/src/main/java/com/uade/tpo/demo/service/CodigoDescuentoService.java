package com.uade.tpo.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.uade.tpo.demo.entity.CodigoDescuento;
import com.uade.tpo.demo.entity.dto.DescuentoValidacionResponse;
import com.uade.tpo.demo.exceptions.CodigoDescuentoDuplicateException;
import com.uade.tpo.demo.exceptions.CodigoDescuentoInvalidoException;
import com.uade.tpo.demo.exceptions.CodigoDescuentoNotFoundException;

public interface CodigoDescuentoService {
    // Solo ADMIN (ver SecurityConfig): activos e inactivos.
    List<CodigoDescuento> getCodigos();

    CodigoDescuento createCodigo(String codigo, Integer porcentaje, LocalDate fechaVencimiento)
            throws CodigoDescuentoDuplicateException;

    // El codigo no se edita; fechaVencimiento null = sin vencimiento.
    CodigoDescuento updateCodigo(Long id, Integer porcentaje, LocalDate fechaVencimiento)
            throws CodigoDescuentoNotFoundException;

    void deleteCodigo(Long id) throws CodigoDescuentoNotFoundException;

    CodigoDescuento reactivarCodigo(Long id) throws CodigoDescuentoNotFoundException;

    // Devuelve el codigo si existe, esta activo y no vencio (sin distinguir mayusculas); si no, lanza.
    CodigoDescuento obtenerValido(String codigo) throws CodigoDescuentoInvalidoException;

    // Para mostrar el descuento en el checkout antes de pagar. Un codigo no valido NO lanza: valido = false.
    DescuentoValidacionResponse validar(String codigo, BigDecimal subtotal);
}
