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

    // Atributos adicionales para mostrar nombres (no se guardan en BD)
    private transient String categoriaNombre;
    private transient String marcaNombre;

    // Getters y Setters
    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public String getMarcaNombre() {
        return marcaNombre;
    }

    public void setMarcaNombre(String marcaNombre) {
        this.marcaNombre = marcaNombre;
    }
}
