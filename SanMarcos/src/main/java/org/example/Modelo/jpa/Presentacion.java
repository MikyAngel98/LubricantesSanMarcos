package org.example.Modelo.jpa;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "Presentacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Presentacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(length = 50)
    private String Nombre;

    @Column(length = 30)
    private String Litros;

    @Override
    public String toString() {
        return Nombre + " (" + Litros + ")";
    }
}
