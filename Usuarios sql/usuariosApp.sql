CREATE TABLE Usuario (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    NombreUsuario VARCHAR(50) NOT NULL UNIQUE,
    Contrasenia VARCHAR(255) NOT NULL,
    NombreCompleto VARCHAR(100) NOT NULL,
    Rol VARCHAR(20) NOT NULL DEFAULT 'VENDEDOR',
    Activo BIT DEFAULT 1,
    FechaCreacion DATETIME DEFAULT GETDATE()
);

-- Insertar usuario ADMIN (contraseña: admin123)
INSERT INTO Usuario (NombreUsuario, Contrasenia, NombreCompleto, Rol)
VALUES ('Lescano', '$2a$10$CWxhw0r9bstAeM4vgda54eJck4wLOrJGab3RZ3c7RiclZbYFRs3Ty', 'Jose Luis Lescano', 'ADMIN');

-- Insertar usuario VENDEDOR (contraseña: ventas123)
INSERT INTO Usuario (NombreUsuario, Contrasenia, NombreCompleto, Rol)
VALUES ('Ema', '$2a$10$X6F7k.68Bk9tB5P4dR7W/OgL35/0Mg..lUD5yKYansw94B7aRh7EC', 'Emanuel', 'VENDEDOR');