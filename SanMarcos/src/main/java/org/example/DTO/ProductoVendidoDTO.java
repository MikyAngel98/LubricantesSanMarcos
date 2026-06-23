package org.example.DTO;

public class ProductoVendidoDTO {
    private int id;
    private String nombre;
    private String categoria;
    private String marca;
    private float totalVendido;
    private double totalFacturado;

    public ProductoVendidoDTO() {}

    public ProductoVendidoDTO(int id, String nombre, String categoria, String marca, float totalVendido, double totalFacturado) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.marca = marca;
        this.totalVendido = totalVendido;
        this.totalFacturado = totalFacturado;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public float getTotalVendido() { return totalVendido; }
    public void setTotalVendido(float totalVendido) { this.totalVendido = totalVendido; }
    public double getTotalFacturado() { return totalFacturado; }
    public void setTotalFacturado(double totalFacturado) { this.totalFacturado = totalFacturado; }
}