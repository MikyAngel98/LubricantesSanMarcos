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
public class Venta {
    private int Id;
    private LocalDate Fecha;
    private Float Total;
    private Integer IdCliente;  // Puede ser null
    private List<DetalleVenta> Detalles = new ArrayList<>();
}
