package com.uade.tpo.demo.repository;
import com.uade.tpo.demo.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrdenRepository extends JpaRepository<Orden, Long> {
}
