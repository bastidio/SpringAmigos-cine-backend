package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Producto;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

public class ProductoSpecifications {

    private ProductoSpecifications() {
    }

    public static Specification<Producto> nombreContiene(String nombre) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");
    }

    public static Specification<Producto> categoriaEs(Long categoriaId) {
        return (root, query, cb) -> cb.equal(root.get("categoria").get("id"), categoriaId);
    }

    public static Specification<Producto> precioMayorOIgualQue(BigDecimal precioMin) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("precio"), precioMin);
    }

    public static Specification<Producto> precioMenorOIgualQue(BigDecimal precioMax) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("precio"), precioMax);
    }

    public static Specification<Producto> estaActivo() {
        return (root, query, cb) -> cb.isTrue(root.get("activo"));
    }

    // Baja logica: activo = false. Incluye tambien filas con activo NULL por si
    // alguna quedo asi de una migracion vieja (cb.isFalse las excluiria).
    public static Specification<Producto> noEstaActivo() {
        return (root, query, cb) -> cb.or(cb.isFalse(root.get("activo")), cb.isNull(root.get("activo")));
    }

}
