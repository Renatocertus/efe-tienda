package com.efe.tienda.dto;

import com.efe.tienda.model.Producto;
import java.math.BigDecimal;

public class ProductoDTO {
    private Long id;
    private String nombre;
    private String modelo;
    private String sku;
    private BigDecimal precio;
    private BigDecimal oferta;
    private Integer stock;
    private Integer stockMinimo;
    private String descripcion;
    private String icon;
    private String imagenUrl;
    private Long categoriaId;
    private String categoriaNombre;
    private Long marcaId;
    private String marcaNombre;

    public ProductoDTO() {}

    public ProductoDTO(Producto p) {
        if (p == null) return;
        this.id = p.getId();
        this.nombre = p.getNombre();
        this.modelo = p.getModelo();
        this.sku = p.getSku();
        this.precio = p.getPrecio();
        this.oferta = p.getOferta() != null ? p.getOferta() : BigDecimal.ZERO;
        this.stock = p.getStock() != null ? p.getStock() : 0;
        this.stockMinimo = p.getStockMinimo() != null ? p.getStockMinimo() : 5;
        this.descripcion = p.getDescripcion();
        this.icon = p.getIcon() != null ? p.getIcon() : "📦";
        this.imagenUrl = p.getImagenUrl();
        try {
            if (p.getCategoria() != null) {
                this.categoriaId = p.getCategoria().getId();
                this.categoriaNombre = p.getCategoria().getNombre();
            }
        } catch (Exception ignored) {}
        try {
            if (p.getMarca() != null) {
                this.marcaId = p.getMarca().getId();
                this.marcaNombre = p.getMarca().getNombre();
            }
        } catch (Exception ignored) {}
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public BigDecimal getOferta() { return oferta; }
    public void setOferta(BigDecimal oferta) { this.oferta = oferta; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

    public Long getMarcaId() { return marcaId; }
    public void setMarcaId(Long marcaId) { this.marcaId = marcaId; }

    public String getMarcaNombre() { return marcaNombre; }
    public void setMarcaNombre(String marcaNombre) { this.marcaNombre = marcaNombre; }
}
