package org.example.Modelo.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Compra {
    private int Id;
    private LocalDate Fecha;
    private Float Total;
    private int IdProveedor;
    private List<DetalleCompra> Detalles = new ArrayList<>();
}
