package org.example.Vista.controllers;

public class ItemCompra {
    private int idProducto;
    private String nombreProducto;
    private float cantidad;
    private float precio;
    private float subtotal;

    public ItemCompra(int idProducto, String nombreProducto, float cantidad, float precio) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precio = precio;
        calcularSubtotal();
    }

    public void calcularSubtotal() {
        this.subtotal = cantidad * precio;
    }

    // Getters y Setters
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public float getCantidad() { return cantidad; }
    public void setCantidad(float cantidad) { this.cantidad = cantidad; calcularSubtotal(); }
    public float getPrecio() { return precio; }
    public void setPrecio(float precio) { this.precio = precio; calcularSubtotal(); }
    public float getSubtotal() { return subtotal; }
}
