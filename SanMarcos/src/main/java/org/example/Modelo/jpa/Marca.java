package org.example.Modelo.jpa;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "Marca")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(nullable = false, length = 30)
    private String Nombre;

    @Override
    public String toString() {
        return Nombre;
    }
}
