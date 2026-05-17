package org.example.Modelo.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Filtro extends Producto {
    private String Codigo;
    private String Rosca;
    private String Uso;

    private transient String marcaNombre;

    public String getMarcaNombre() { return marcaNombre; }
    public void setMarcaNombre(String marcaNombre) { this.marcaNombre = marcaNombre; }
}
