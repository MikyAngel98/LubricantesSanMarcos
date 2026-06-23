package org.example.DTO;

import java.time.LocalDate;

public class HistorialCompraDTO {
    private int id;
    private LocalDate fecha;
    private String proveedor;
    private int tiposProductos;
    private double total;

    public HistorialCompraDTO() {}

    public HistorialCompraDTO(int id, LocalDate fecha, String proveedor, int tiposProductos, double total) {
        this.id = id;
        this.fecha = fecha;
        this.proveedor = proveedor;
        this.tiposProductos = tiposProductos;
        this.total = total;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    public int getTiposProductos() { return tiposProductos; }
    public void setTiposProductos(int tiposProductos) { this.tiposProductos = tiposProductos; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}