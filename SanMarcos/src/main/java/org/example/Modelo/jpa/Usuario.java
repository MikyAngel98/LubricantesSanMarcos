package org.example.Modelo.jpa;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(name = "NombreUsuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @Column(name = "Contrasenia", nullable = false, length = 255)
    private String contrasenia;

    @Column(name = "NombreCompleto", nullable = false, length = 100)
    private String nombreCompleto;

    @Column(name = "Rol", nullable = false, length = 20)
    private String rol;

    @Column(name = "Activo")
    private Boolean activo = true;

    @Column(name = "FechaCreacion")
    private LocalDateTime fechaCreacion;
}
