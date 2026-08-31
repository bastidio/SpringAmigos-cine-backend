package com.uade.tpo.demo.repository;
import com.uade.tpo.demo.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByUsuarioId(Long usuarioId);
}
