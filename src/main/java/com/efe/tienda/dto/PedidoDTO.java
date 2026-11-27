package com.efe.tienda.dto;

import com.efe.tienda.model.Pedido;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class PedidoDTO {
    private Long id;
    private String codigo;
    private String estado;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private BigDecimal costoEnvio;
    private String metodoEntrega;
    private String direccionEnvio;
    private String fechaEmision;
    private Long clienteId;
    private String clienteNombre;
    private String clienteDocumento;

    public PedidoDTO() {}

    public PedidoDTO(Pedido p) {
        if (p == null) return;
        this.id = p.getId();
        this.codigo = p.getCodigo();
        this.estado = p.getEstado();
        this.subtotal = p.getSubtotal();
        this.igv = p.getIgv();
        this.total = p.getTotal();
        this.costoEnvio = p.getCostoEnvio();
        this.metodoEntrega = p.getMetodoEntrega();
        this.direccionEnvio = p.getDireccionEnvio();
        if (p.getFechaEmision() != null) {
            this.fechaEmision = p.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        try {
            if (p.getCliente() != null) {
                this.clienteId = p.getCliente().getId();
                this.clienteNombre = p.getCliente().getNombres() + " " + p.getCliente().getApellidos();
                this.clienteDocumento = p.getCliente().getNumeroDocumento();
            }
        } catch (Exception ignored) {}
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getIgv() { return igv; }
    public void setIgv(BigDecimal igv) { this.igv = igv; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getCostoEnvio() { return costoEnvio; }
    public void setCostoEnvio(BigDecimal costoEnvio) { this.costoEnvio = costoEnvio; }

    public String getMetodoEntrega() { return metodoEntrega; }
    public void setMetodoEntrega(String metodoEntrega) { this.metodoEntrega = metodoEntrega; }

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public String getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(String fechaEmision) { this.fechaEmision = fechaEmision; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getClienteDocumento() { return clienteDocumento; }
    public void setClienteDocumento(String clienteDocumento) { this.clienteDocumento = clienteDocumento; }
}
