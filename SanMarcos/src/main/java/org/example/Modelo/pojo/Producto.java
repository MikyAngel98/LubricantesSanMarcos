package org.example.Modelo.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    private int Id;
    private String Nombre;
    private Float Precio;
    private Float Stock;
    private String Detalle;
    private int IdCategoria;
    private int IdMarca;
}
