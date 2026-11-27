package com.efe.tienda.dto;

import com.efe.tienda.model.MovimientoStock;
import java.time.format.DateTimeFormatter;

public class MovimientoStockDTO {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private String tipo;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockPosterior;
    private String motivo;
    private String usuarioResponsable;
    private String fechaMovimiento;

    public MovimientoStockDTO() {}

    public MovimientoStockDTO(MovimientoStock m) {
        if (m == null) return;
        this.id = m.getId();
        this.tipo = m.getTipo();
        this.cantidad = m.getCantidad();
        this.stockAnterior = m.getStockAnterior();
        this.stockPosterior = m.getStockPosterior();
        this.motivo = m.getMotivo();
        this.usuarioResponsable = m.getUsuarioResponsable();
        if (m.getFechaMovimiento() != null) {
            this.fechaMovimiento = m.getFechaMovimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }
        try {
            if (m.getProducto() != null) {
                this.productoId = m.getProducto().getId();
                this.productoNombre = m.getProducto().getNombre();
            }
        } catch (Exception ignored) {}
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Integer getStockAnterior() { return stockAnterior; }
    public void setStockAnterior(Integer stockAnterior) { this.stockAnterior = stockAnterior; }

    public Integer getStockPosterior() { return stockPosterior; }
    public void setStockPosterior(Integer stockPosterior) { this.stockPosterior = stockPosterior; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getUsuarioResponsable() { return usuarioResponsable; }
    public void setUsuarioResponsable(String usuarioResponsable) { this.usuarioResponsable = usuarioResponsable; }

    public String getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(String fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }
}
