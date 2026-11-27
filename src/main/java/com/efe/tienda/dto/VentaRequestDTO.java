package com.efe.tienda.dto;

import java.math.BigDecimal;
import java.util.List;

public class VentaRequestDTO {
    // Datos del cliente
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String direccion;

    // Datos del pedido
    private String metodoEntrega;
    private String direccionEnvio;
    private BigDecimal costoEnvio;
    private List<ItemVentaDTO> items;

    // Datos de facturación
    private String tipoComprobante; // BOLETA o FACTURA
    private String razonSocial;     // Solo para factura (opcional)

    public VentaRequestDTO() {}

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getMetodoEntrega() { return metodoEntrega; }
    public void setMetodoEntrega(String metodoEntrega) { this.metodoEntrega = metodoEntrega; }

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public BigDecimal getCostoEnvio() { return costoEnvio; }
    public void setCostoEnvio(BigDecimal costoEnvio) { this.costoEnvio = costoEnvio; }

    public List<ItemVentaDTO> getItems() { return items; }
    public void setItems(List<ItemVentaDTO> items) { this.items = items; }

    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
}
