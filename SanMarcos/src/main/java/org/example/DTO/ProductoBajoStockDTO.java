package org.example.DTO;

public class ProductoBajoStockDTO {
    private int id;
    private String nombre;
    private String categoria;
    private String marca;
    private float stock;
    private float precio;

    public ProductoBajoStockDTO() {}

    public ProductoBajoStockDTO(int id, String nombre, String categoria, String marca, float stock, float precio) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.marca = marca;
        this.stock = stock;
        this.precio = precio;
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
    public float getStock() { return stock; }
    public void setStock(float stock) { this.stock = stock; }
    public float getPrecio() { return precio; }
    public void setPrecio(float precio) { this.precio = precio; }
}