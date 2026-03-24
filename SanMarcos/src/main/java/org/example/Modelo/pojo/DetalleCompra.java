package org.example.Modelo.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCompra {
    private int Id;
    private Float Cantidad;
    private Float PrecioCompra;
    private int IdProducto;
    private int IdCompra;
}
