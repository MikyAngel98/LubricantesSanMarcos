package org.example.DTO;

import java.time.LocalDate;

public class ReporteVentaDTO {
    private int id;
    private LocalDate fecha;
    private String cliente;
    private int tiposProductos;
    private double total;
    private String metodoPago;

    public ReporteVentaDTO() {}

    public ReporteVentaDTO(int id, LocalDate fecha, String cliente, int tiposProductos, double total, String metodoPago) {
        this.id = id;
        this.fecha = fecha;
        this.cliente = cliente;
        this.tiposProductos = tiposProductos;
        this.total = total;
        this.metodoPago = metodoPago;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public int getTiposProductos() { return tiposProductos; }
    public void setTiposProductos(int tiposProductos) { this.tiposProductos = tiposProductos; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
}