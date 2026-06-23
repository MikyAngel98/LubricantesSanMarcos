package org.example.DTO;

public class DetalleVentaDTO {
    private String producto;
    private String viscosidad;
    private String codigo;
    private String marca;
    private String categoria;
    private float cantidad;
    private float precioVenta;
    private float subtotal;

    // Constructores
    public DetalleVentaDTO() {}

    public DetalleVentaDTO(String producto, String viscosidad, String codigo, String marca,
                           String categoria, float cantidad, float precioVenta, float subtotal) {
        this.producto = producto;
        this.viscosidad = viscosidad;
        this.codigo = codigo;
        this.marca = marca;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.precioVenta = precioVenta;
        this.subtotal = subtotal;
    }

    // Getters y Setters
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }

    public String getViscosidad() { return viscosidad; }
    public void setViscosidad(String viscosidad) { this.viscosidad = viscosidad; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public float getCantidad() { return cantidad; }
    public void setCantidad(float cantidad) { this.cantidad = cantidad; }

    public float getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(float precioVenta) { this.precioVenta = precioVenta; }

    public float getSubtotal() { return subtotal; }
    public void setSubtotal(float subtotal) { this.subtotal = subtotal; }
}