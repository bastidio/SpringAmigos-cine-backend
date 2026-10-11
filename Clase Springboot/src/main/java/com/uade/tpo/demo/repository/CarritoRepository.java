package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuarioId(Long usuarioId);

    // Igual que findByUsuarioId pero bloqueando la fila (SELECT ... FOR UPDATE) hasta que termine
    // la transaccion. Asi el checkout y la limpieza por vencimiento no pisan el mismo carrito a la vez.
    // Solo se puede usar dentro de una transaccion.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Carrito> findForUpdateByUsuarioId(Long usuarioId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Carrito> findForUpdateById(Long id);
    List<Carrito> findAllByExpiraEnBefore(LocalDateTime momento);
}
