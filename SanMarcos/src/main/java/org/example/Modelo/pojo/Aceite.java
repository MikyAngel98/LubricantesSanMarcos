package org.example.Modelo.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Aceite extends Producto {
    private String Viscosidad;
    private String TipoAceite;
    private String Uso;
    private boolean EsAgranel;
    private int IdPresentacion;

    private transient String marcaNombre;
    private transient String presentacionNombre;

    // Getters y Setters
    public String getMarcaNombre() {
        return marcaNombre;
    }

    public void setMarcaNombre(String marcaNombre) {
        this.marcaNombre = marcaNombre;
    }

    public String getPresentacionNombre() {
        return presentacionNombre;
    }

    public void setPresentacionNombre(String presentacionNombre) {
        this.presentacionNombre = presentacionNombre;
    }
}
