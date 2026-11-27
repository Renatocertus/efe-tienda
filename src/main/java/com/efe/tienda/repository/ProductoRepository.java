package com.efe.tienda.repository;

import com.efe.tienda.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findBySku(String sku);
    List<Producto> findByActivoTrue();
    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);
    List<Producto> findByMarcaIdAndActivoTrue(Long marcaId);
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String query);

    // Consulta para alertar stock bajo o agotado
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo AND p.activo = true")
    List<Producto> findProductosConBajoStock();

    // Contar productos con stock crítico
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock <= p.stockMinimo AND p.activo = true")
    long countBajoStock();
}
