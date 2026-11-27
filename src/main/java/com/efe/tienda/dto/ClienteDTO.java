package com.efe.tienda.dto;

import com.efe.tienda.model.Cliente;
import java.time.format.DateTimeFormatter;

public class ClienteDTO {
    private Long id;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String direccion;
    private String fechaRegistro;

    public ClienteDTO() {}

    public ClienteDTO(Cliente c) {
        if (c == null) return;
        this.id = c.getId();
        this.tipoDocumento = c.getTipoDocumento();
        this.numeroDocumento = c.getNumeroDocumento();
        this.nombres = c.getNombres();
        this.apellidos = c.getApellidos();
        this.correo = c.getCorreo();
        this.telefono = c.getTelefono();
        this.direccion = c.getDireccion();
        if (c.getFechaRegistro() != null) {
            this.fechaRegistro = c.getFechaRegistro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
