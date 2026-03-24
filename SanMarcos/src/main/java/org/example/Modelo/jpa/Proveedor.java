package org.example.Modelo.jpa;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "Proveedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "IdPersona", nullable = false)
    private Persona persona;

    @Column(length = 20)
    private String Empresa;

}
