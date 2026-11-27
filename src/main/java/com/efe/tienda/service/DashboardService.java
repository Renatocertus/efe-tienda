package com.efe.tienda.service;

import com.efe.tienda.dto.DashboardResponseDTO;
import com.efe.tienda.repository.PedidoRepository;
import com.efe.tienda.repository.ProductoRepository;
import com.efe.tienda.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DashboardService {

    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public DashboardService(ProductoRepository productoRepository,
                            PedidoRepository pedidoRepository,
                            UsuarioRepository usuarioRepository) {
        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public DashboardResponseDTO obtenerResumen() {
        DashboardResponseDTO dto = new DashboardResponseDTO();
        dto.setTotalProductos(productoRepository.count());
        dto.setTotalUsuarios(usuarioRepository.count());
        dto.setTotalPersonal(usuarioRepository.findByEsPersonalTrue().size());
        dto.setTotalPedidos(pedidoRepository.count());
        dto.setPedidosPendientes(pedidoRepository.countByEstado("PENDIENTE"));
        dto.setProductosBajoStock(productoRepository.countBajoStock());

        BigDecimal totalVentas = pedidoRepository.sumTotalVentas();
        dto.setTotalVentas(totalVentas != null ? totalVentas : BigDecimal.ZERO);

        return dto;
    }
}
