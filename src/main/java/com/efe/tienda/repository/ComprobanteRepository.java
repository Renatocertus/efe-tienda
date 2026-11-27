package com.efe.tienda.repository;

import com.efe.tienda.model.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {
    Optional<Comprobante> findByPedidoId(Long pedidoId);
    Optional<Comprobante> findByTipoComprobanteAndSerieAndNumero(String tipo, String serie, String numero);
    long countByTipoComprobanteAndSerie(String tipo, String serie);
}
