package com.efe.tienda.repository;

import com.efe.tienda.model.MovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {
    List<MovimientoStock> findByProductoIdOrderByFechaMovimientoDesc(Long productoId);
    List<MovimientoStock> findTop50ByOrderByFechaMovimientoDesc();
}
