package com.efe.tienda.repository;

import com.efe.tienda.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByCodigo(String codigo);
    List<Pedido> findByClienteIdOrderByFechaEmisionDesc(Long clienteId);
    List<Pedido> findByEstadoOrderByFechaEmisionDesc(String estado);
    List<Pedido> findAllByOrderByFechaEmisionDesc();

    long countByEstado(String estado);

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.estado <> 'CANCELADO'")
    BigDecimal sumTotalVentas();
}
