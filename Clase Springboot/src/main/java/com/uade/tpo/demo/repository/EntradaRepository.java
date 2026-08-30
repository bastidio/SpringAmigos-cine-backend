package com.uade.tpo.demo.repository;

import org.springframework.stereotype.Repository;
import com.uade.tpo.demo.entity.Entrada; 
import org.springframework.data.jpa.repository.JpaRepository;
@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Long> {
}
