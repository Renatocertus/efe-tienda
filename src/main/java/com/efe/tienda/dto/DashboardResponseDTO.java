package com.efe.tienda.dto;

import java.math.BigDecimal;

public class DashboardResponseDTO {
    private long totalProductos;
    private long totalUsuarios;
    private long totalPersonal;
    private long totalPedidos;
    private long pedidosPendientes;
    private long productosBajoStock;
    private BigDecimal totalVentas;

    public DashboardResponseDTO() {}

    public long getTotalProductos() { return totalProductos; }
    public void setTotalProductos(long totalProductos) { this.totalProductos = totalProductos; }

    public long getTotalUsuarios() { return totalUsuarios; }
    public void setTotalUsuarios(long totalUsuarios) { this.totalUsuarios = totalUsuarios; }

    public long getTotalPersonal() { return totalPersonal; }
    public void setTotalPersonal(long totalPersonal) { this.totalPersonal = totalPersonal; }

    public long getTotalPedidos() { return totalPedidos; }
    public void setTotalPedidos(long totalPedidos) { this.totalPedidos = totalPedidos; }

    public long getPedidosPendientes() { return pedidosPendientes; }
    public void setPedidosPendientes(long pedidosPendientes) { this.pedidosPendientes = pedidosPendientes; }

    public long getProductosBajoStock() { return productosBajoStock; }
    public void setProductosBajoStock(long productosBajoStock) { this.productosBajoStock = productosBajoStock; }

    public BigDecimal getTotalVentas() { return totalVentas; }
    public void setTotalVentas(BigDecimal totalVentas) { this.totalVentas = totalVentas; }
}
