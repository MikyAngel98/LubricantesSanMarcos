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
}
