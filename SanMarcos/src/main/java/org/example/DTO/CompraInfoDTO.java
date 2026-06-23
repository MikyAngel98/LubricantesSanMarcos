package org.example.DTO;

import java.time.LocalDate;

public class CompraInfoDTO {
    private LocalDate fecha;
    private String proveedor;

    public CompraInfoDTO() {}

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
}
