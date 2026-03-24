package org.example.Modelo.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta {
    private int Id;
    private Float Cantidad;
    private Float PrecioVenta;
    private int IdProducto;
    private int IdVenta;
}
