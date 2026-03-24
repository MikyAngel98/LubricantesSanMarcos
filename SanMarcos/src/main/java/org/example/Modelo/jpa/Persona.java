package org.example.Modelo.jpa;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "Persona")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(length = 50)
    private String Nombres;

    @Column(length = 50)
    private String Apellidos;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "IdContacto", nullable = false)
    private Contacto contacto;

    public String getNombreCompleto() {
        return Nombres + " " + Apellidos;
    }

}
