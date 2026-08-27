package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.entity.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsientoRepository extends JpaRepository<Asiento, Long> {
}