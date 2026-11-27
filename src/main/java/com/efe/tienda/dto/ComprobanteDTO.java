package com.efe.tienda.dto;

import com.efe.tienda.model.Comprobante;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class ComprobanteDTO {
    private Long id;
    private String tipoComprobante;
    private String serie;
    private String numero;
    private String clienteDocumento;
    private String clienteRazonSocial;
    private String clienteDireccion;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private String fechaEmision;
    private String estadoSunat;

    public ComprobanteDTO() {}

    public ComprobanteDTO(Comprobante c) {
        if (c == null) return;
        this.id = c.getId();
        this.tipoComprobante = c.getTipoComprobante();
        this.serie = c.getSerie();
        this.numero = c.getNumero();
        this.clienteDocumento = c.getClienteDocumento();
        this.clienteRazonSocial = c.getClienteRazonSocial();
        this.clienteDireccion = c.getClienteDireccion();
        this.subtotal = c.getSubtotal();
        this.igv = c.getIgv();
        this.total = c.getTotal();
        this.estadoSunat = c.getEstadoSunat();
        if (c.getFechaEmision() != null) {
            this.fechaEmision = c.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getClienteDocumento() { return clienteDocumento; }
    public void setClienteDocumento(String clienteDocumento) { this.clienteDocumento = clienteDocumento; }

    public String getClienteRazonSocial() { return clienteRazonSocial; }
    public void setClienteRazonSocial(String clienteRazonSocial) { this.clienteRazonSocial = clienteRazonSocial; }

    public String getClienteDireccion() { return clienteDireccion; }
    public void setClienteDireccion(String clienteDireccion) { this.clienteDireccion = clienteDireccion; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getIgv() { return igv; }
    public void setIgv(BigDecimal igv) { this.igv = igv; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(String fechaEmision) { this.fechaEmision = fechaEmision; }

    public String getEstadoSunat() { return estadoSunat; }
    public void setEstadoSunat(String estadoSunat) { this.estadoSunat = estadoSunat; }
}
